package com.hackaton.module.record.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.dto.RecordPage;
import com.hackaton.module.record.dto.RecordQuery;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.reader.ModuleRecordReaderService;
import com.hackaton.module.record.service.reader.RecordSearchFilter;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleRecordReaderServiceTest {

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private ModuleRecordReaderService reader;

    @BeforeEach
    void setUp() {
        reader = new ModuleRecordReaderService(new DefinitionLookup(definitionRepository), recordRepository,
                new RecordSearchFilter(), new ModuleRecordMapper());
    }

    @Test
    void shouldFilterByStatusAndSearch_andCountAllMatches() {
        givenRecords();

        RecordPage page = reader.search("livraisons", new RecordQuery("antsirabe", "en_route", 0, 20));

        assertEquals(1, page.total());
        assertEquals(List.of(2L), page.items().stream().map(ModuleRecordResponse::id).toList());
    }

    @Test
    void shouldReturnRequestedPage_withTotalOfAllRecords() {
        givenRecords();

        RecordPage page = reader.search("livraisons", new RecordQuery(null, " ", 1, 2));

        assertEquals(3, page.total());
        assertEquals(List.of(3L), page.items().stream().map(ModuleRecordResponse::id).toList());
    }

    @Test
    void shouldRefuse_whenStatusIsUnknown() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));

        assertThrows(InvalidInputException.class,
                () -> reader.search("livraisons", new RecordQuery(null, "perdue", 0, 20)));
    }

    @Test
    void shouldRefuse_whenPaginationIsOutOfRange() {
        assertThrows(InvalidInputException.class, () -> reader.search("livraisons", new RecordQuery(null, null, -1, 20)));
        assertThrows(InvalidInputException.class, () -> reader.search("livraisons", new RecordQuery(null, null, 0, 101)));
        verifyNoInteractions(definitionRepository, recordRepository);
    }

    private void givenRecords() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
        when(recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(10L)).thenReturn(List.of(
                RecordFixtures.record(definition, 1, "en_route", Map.of("quartier", "Toamasina"), TestClocks.NOW),
                RecordFixtures.record(definition, 2, "en_route", Map.of("quartier", "Antsirabe"), TestClocks.NOW),
                RecordFixtures.record(definition, 3, "a_preparer", Map.of("quartier", "Antsirabe"), TestClocks.NOW)));
    }
}
