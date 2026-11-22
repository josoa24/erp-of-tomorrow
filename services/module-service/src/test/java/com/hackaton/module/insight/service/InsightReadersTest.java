package com.hackaton.module.insight.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.repository.ModuleDefinitionRepository;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.insight.dto.StatusCountsResponse;
import com.hackaton.module.record.repository.ModuleRecordRepository;
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
class InsightReadersTest {

    @Mock
    private ModuleDefinitionRepository definitionRepository;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();
    private DefinitionLookup lookup;

    @BeforeEach
    void setUp() {
        lookup = new DefinitionLookup(definitionRepository);
    }

    @Test
    void shouldComputeEveryIndicatorInDefinitionOrder() {
        givenRecords();
        IndicatorReaderService reader = new IndicatorReaderService(lookup, recordRepository,
                new IndicatorCalculator(new TodayScope(TestClocks.SUNDAY_NOON)));

        List<IndicatorValueResponse> indicators = reader.compute("livraisons");

        assertEquals(List.of("livrees_jour", "en_route", "a_encaisser"),
                indicators.stream().map(IndicatorValueResponse::key).toList());
    }

    @Test
    void shouldCountByStatus_includingStatusesWithoutRecords() {
        givenRecords();

        StatusCountsResponse counts = new StatusCountReaderService(lookup, recordRepository).count("livraisons");

        assertEquals(3, counts.all());
        assertEquals(List.of("a_preparer", "en_route", "livree"), List.copyOf(counts.byStatus().keySet()));
        assertEquals(Map.of("a_preparer", 2L, "en_route", 0L, "livree", 1L), counts.byStatus());
    }

    @Test
    void shouldThrowNotFound_whenModuleIsUnknown() {
        when(definitionRepository.findBySlug("inconnu")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> new StatusCountReaderService(lookup, recordRepository).count("inconnu"));
    }

    private void givenRecords() {
        when(definitionRepository.findBySlug("livraisons")).thenReturn(Optional.of(definition));
        when(recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(10L)).thenReturn(List.of(
                RecordFixtures.record(definition, 1, "a_preparer", Map.of(), TestClocks.NOW),
                RecordFixtures.record(definition, 2, "a_preparer", Map.of(), TestClocks.NOW),
                RecordFixtures.record(definition, 3, "livree", Map.of(), TestClocks.NOW)));
    }
}
