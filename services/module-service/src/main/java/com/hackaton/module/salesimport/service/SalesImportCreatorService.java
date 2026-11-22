package com.hackaton.module.salesimport.service;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.creator.RecordFactory;
import com.hackaton.module.salesimport.dto.SalesImportResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesImportCreatorService implements SalesImportCreator {

    private final DefinitionLookup definitionLookup;
    private final SaleCandidateFinder candidateFinder;
    private final ImportedValuesBuilder valuesBuilder;
    private final RecordFactory recordFactory;
    private final ModuleRecordRepository recordRepository;

    @Override
    @Transactional
    public SalesImportResult importSales(String slug, Caller caller) {
        ModuleDefinition definition = definitionLookup.require(slug);
        ImportColumns columns = ImportColumns.of(definition);
        Map<Long, Optional<String>> customerNames = new HashMap<>();
        List<ModuleRecord> records = candidateFinder.candidates(definition, columns).stream()
                .map(sale -> recordFactory.initial(definition,
                        valuesBuilder.build(sale, columns, customerNames), caller.author()))
                .toList();
        recordRepository.saveAll(records);
        return new SalesImportResult(records.size());
    }
}
