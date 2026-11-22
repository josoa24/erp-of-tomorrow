package com.hackaton.module.record.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ImmutableResourceException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.entity.StatusTone;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.updater.ModuleRecordUpdaterService;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ModuleRecordUpdaterServiceTest {

    private static final Instant CREATED = Instant.parse("2026-11-21T06:00:00Z");

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private ModuleRecordUpdaterService updater;

    @BeforeEach
    void setUp() {
        updater = new ModuleRecordUpdaterService(new DefinitionLookup(definitionRepository), recordRepository,
                new ModuleRecordMapper(), TestClocks.SUNDAY_NOON);
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
    }

    @Test
    void shouldMoveToNextStatus_andTouchUpdatedAt() {
        givenRecord("a_preparer");
        when(recordRepository.save(any(ModuleRecord.class))).thenAnswer(call -> call.getArgument(0));

        ModuleRecordResponse response = updater.advance("livraisons", 5L);

        assertEquals("en_route", response.status());
        assertEquals(CREATED, response.createdAt());
        assertEquals(TestClocks.NOW, response.updatedAt());
    }

    @Test
    void shouldRefuseWithImmutable_whenRecordIsInFinalStatus() {
        givenRecord("livree");

        assertThrows(ImmutableResourceException.class, () -> updater.advance("livraisons", 5L));
        verify(recordRepository, never()).save(any());
    }

    @Test
    void shouldRefuseWithBusiness_whenLastStatusIsNotFinal() {
        definition.getStatuses().get(2).setFinalStatus(false);
        definition.getStatuses().get(2).setTone(StatusTone.NEUTRAL);
        givenRecord("livree");

        assertThrows(BusinessException.class, () -> updater.advance("livraisons", 5L));
    }

    @Test
    void shouldThrowNotFound_whenRecordBelongsToAnotherModule() {
        when(recordRepository.findByIdAndDefinitionId(6L, 10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> updater.advance("livraisons", 6L));
    }

    private void givenRecord(String status) {
        ModuleRecord record = RecordFixtures.record(definition, 5, status, Map.of(), CREATED);
        when(recordRepository.findByIdAndDefinitionId(5L, 10L)).thenReturn(Optional.of(record));
    }
}
