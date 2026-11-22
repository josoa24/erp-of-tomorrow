package com.hackaton.module.salesimport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.creator.RecordFactory;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalesImportServicesTest {

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private SaleCandidateFinder candidateFinder;
    @Mock
    private ImportedValuesBuilder valuesBuilder;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private final RemoteSale sale = new RemoteSale(12L, 7L, "VALIDATED", BigDecimal.TEN, null, null, true);
    private DefinitionLookup lookup;

    @BeforeEach
    void setUp() {
        lookup = new DefinitionLookup(definitionRepository);
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
    }

    @Test
    void shouldCountAvailableSales() {
        when(candidateFinder.candidates(definition, ImportColumns.of(definition))).thenReturn(List.of(sale, sale));

        assertEquals(2, new SalesImportReaderService(lookup, candidateFinder).availability("livraisons").available());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateOneInitialRecordPerCandidate() {
        when(candidateFinder.candidates(any(), any())).thenReturn(List.of(sale));
        when(valuesBuilder.build(any(), any(), any())).thenReturn(Map.of("vente", Map.of("id", 12, "label", "V-0012")));
        SalesImportCreatorService creator = new SalesImportCreatorService(lookup, candidateFinder, valuesBuilder,
                new RecordFactory(TestClocks.SUNDAY_NOON), recordRepository);

        long created = creator.importSales("livraisons", new Caller("toky.rakoto@nova.mg", "STOCK")).created();

        ArgumentCaptor<List<ModuleRecord>> saved = ArgumentCaptor.forClass(List.class);
        verify(recordRepository).saveAll(saved.capture());
        assertEquals(1, created);
        assertEquals("a_preparer", saved.getValue().get(0).getStatus());
        assertEquals("toky.rakoto@nova.mg", saved.getValue().get(0).getCreatedBy());
    }

    @Test
    void shouldRefuse_whenModuleHasNoSaleColumn() {
        definition.getColumns().removeIf(column -> column.getKey().equals("vente"));

        assertThrows(BusinessException.class,
                () -> new SalesImportReaderService(lookup, candidateFinder).availability("livraisons"));
        verifyNoInteractions(candidateFinder);
    }

    @Test
    void shouldSaveNothing_whenNoSaleIsAvailable() {
        when(candidateFinder.candidates(any(), any())).thenReturn(List.of());
        SalesImportCreatorService creator = new SalesImportCreatorService(lookup, candidateFinder, valuesBuilder,
                new RecordFactory(TestClocks.SUNDAY_NOON), recordRepository);

        assertEquals(0, creator.importSales("livraisons", new Caller(null, "VENTE")).created());
        verify(recordRepository).saveAll(anyList());
    }
}
