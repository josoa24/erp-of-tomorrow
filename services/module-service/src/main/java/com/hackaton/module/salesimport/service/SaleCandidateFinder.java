package com.hackaton.module.salesimport.service;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.values.RecordValues;
import com.hackaton.module.reference.client.RemoteSale;
import com.hackaton.module.reference.client.VenteClient;
import java.time.Clock;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleCandidateFinder {

    static final String VALIDATED = "VALIDATED";

    private final VenteClient venteClient;
    private final ModuleRecordRepository recordRepository;
    private final Clock clock;

    public List<RemoteSale> candidates(ModuleDefinition definition, ImportColumns columns) {
        WeekWindow week = WeekWindow.current(clock);
        Set<Long> referenced = recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(definition.getId())
                .stream()
                .map(record -> RecordValues.referenceId(record.getValues().get(columns.saleKey())))
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());
        return venteClient.findValidatedSalesToDeliver(week.monday().minusDays(1), week.sunday()).stream()
                .filter(sale -> VALIDATED.equals(sale.status()))
                .filter(sale -> Boolean.TRUE.equals(sale.toDeliver()))
                .filter(sale -> week.contains(sale.createdAt()))
                .filter(sale -> !referenced.contains(sale.id()))
                .toList();
    }
}
