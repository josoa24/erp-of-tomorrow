package com.hackaton.module.insight.service;

import com.hackaton.module.definition.entity.ModuleColumn;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.insight.dto.IndicatorValueResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IndicatorReaderService implements IndicatorReader {

    private final DefinitionLookup definitionLookup;
    private final ModuleRecordRepository recordRepository;
    private final IndicatorCalculator calculator;

    @Override
    public List<IndicatorValueResponse> compute(String slug) {
        ModuleDefinition definition = definitionLookup.require(slug);
        Map<String, ModuleColumn> columns = definition.getColumns().stream()
                .collect(Collectors.toMap(ModuleColumn::getKey, Function.identity()));
        List<ModuleRecord> records = recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(definition.getId());
        return definition.getIndicators().stream()
                .map(indicator -> calculator.compute(indicator, columns, records))
                .toList();
    }
}
