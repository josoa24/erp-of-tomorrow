package com.hackaton.module.insight.service;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.insight.dto.StatusCountsResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatusCountReaderService implements StatusCountReader {

    private final DefinitionLookup definitionLookup;
    private final ModuleRecordRepository recordRepository;

    @Override
    public StatusCountsResponse count(String slug) {
        ModuleDefinition definition = definitionLookup.require(slug);
        List<ModuleRecord> records = recordRepository.findByDefinitionIdOrderByCreatedAtDescIdDesc(definition.getId());
        Map<String, Long> byStatus = new LinkedHashMap<>();
        definition.getStatuses().forEach(status -> byStatus.put(status.getKey(), 0L));
        records.forEach(record -> byStatus.merge(record.getStatus(), 1L, Long::sum));
        return new StatusCountsResponse(records.size(), byStatus);
    }
}
