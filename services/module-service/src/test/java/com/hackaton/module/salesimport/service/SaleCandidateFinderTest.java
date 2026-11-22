package com.hackaton.module.salesimport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.reference.client.VenteClient;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.RecordFixtures;
import com.hackaton.module.support.TestClocks;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleCandidateFinderTest {

    @Mock
    private VenteClient venteClient;
    @Mock
    private ModuleRecordRepository recordRepository;

    private final ModuleDefinition definition = DefinitionFixtures.livraisons();

    @Test
    void shouldKeepValidatedSalesToDeliverOfTheWeek_notAlreadyImported() {
        when(recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(10L)).thenReturn(List.of(
                RecordFixtures.record(definition, 1, "a_preparer", Map.of("vente", RecordFixtures.ref(6, "V-0006")),
                        TestClocks.NOW)));
        when(venteClient.findValidatedSalesToDeliver(LocalDate.parse("2026-11-15"), LocalDate.parse("2026-11-22")))
                .thenReturn(List.of(
                        sale(1, "VALIDATED", true, "2026-11-18T07:00:00Z"),
                        sale(2, "VALIDATED", null, "2026-11-18T07:00:00Z"),
                        sale(3, "DRAFT", true, "2026-11-18T07:00:00Z"),
                        sale(4, "VALIDATED", true, "2026-11-15T20:00:00Z"),
                        sale(5, "VALIDATED", true, "2026-11-15T22:00:00Z"),
                        sale(6, "VALIDATED", true, "2026-11-19T07:00:00Z"),
                        sale(7, "VALIDATED", false, "2026-11-19T07:00:00Z")));
        SaleCandidateFinder finder = new SaleCandidateFinder(venteClient, recordRepository, TestClocks.SUNDAY_NOON);

        List<RemoteSale> candidates = finder.candidates(definition, ImportColumns.of(definition));

        assertEquals(List.of(1L, 5L), candidates.stream().map(RemoteSale::id).toList());
    }

    private RemoteSale sale(long id, String status, Boolean toDeliver, String createdAt) {
        return new RemoteSale(id, 7L, status, new BigDecimal("45000"), Instant.parse(createdAt), null, toDeliver);
    }
}
