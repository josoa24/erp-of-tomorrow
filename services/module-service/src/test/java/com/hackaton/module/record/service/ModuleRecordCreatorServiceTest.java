package com.hackaton.module.record.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.InvalidInputException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.dto.RecordRequest;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.creator.ModuleRecordCreatorService;
import com.hackaton.module.record.service.creator.RecordFactory;
import com.hackaton.module.record.service.values.RecordValuesValidator;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.TestClocks;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleRecordCreatorServiceTest {

    private static final Caller SELLER = new Caller("rasoa.andria@nova.mg", "VENTE");

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private RecordValuesValidator valuesValidator;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private ModuleRecordCreatorService creator;

    @BeforeEach
    void setUp() {
        creator = new ModuleRecordCreatorService(new DefinitionLookup(definitionRepository), valuesValidator,
                new RecordFactory(TestClocks.SUNDAY_NOON), recordRepository, new ModuleRecordMapper());
    }

    @Test
    void shouldSaveRecordInInitialStatus_withValidatedValuesAndAuthor() {
        Map<String, Object> raw = Map.of("quartier", "Isotry");
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
        when(valuesValidator.validate(definition, raw)).thenReturn(Map.of("quartier", "Isotry"));
        when(recordRepository.save(any(ModuleRecord.class))).thenAnswer(call -> call.getArgument(0));

        ModuleRecordResponse response = creator.create("livraisons", new RecordRequest(raw), SELLER);

        assertEquals("a_preparer", response.status());
        assertEquals("rasoa.andria@nova.mg", response.createdBy());
        assertEquals(TestClocks.NOW, response.createdAt());
        assertEquals(TestClocks.NOW, response.updatedAt());
        assertEquals("Isotry", response.values().get("quartier"));
    }

    @Test
    void shouldNotSave_whenValuesAreInvalid() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
        when(valuesValidator.validate(any(), any())).thenThrow(new InvalidInputException("Champ obligatoire : Client"));

        assertThrows(InvalidInputException.class,
                () -> creator.create("livraisons", new RecordRequest(Map.of()), SELLER));
        verify(recordRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFound_whenModuleIsUnknown() {
        when(definitionRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> creator.create("inconnu", new RecordRequest(Map.of()), SELLER));
    }
}
