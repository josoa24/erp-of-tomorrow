package com.hackaton.module.record.service.creator;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.definition.service.reader.DefinitionLookup;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.dto.RecordRequest;
import com.hackaton.module.record.mapper.ModuleRecordMapper;
import com.hackaton.module.record.repository.ModuleRecordRepository;
import com.hackaton.module.record.service.values.RecordValuesValidator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModuleRecordCreatorService implements ModuleRecordCreator {

    private final DefinitionLookup definitionLookup;
    private final RecordValuesValidator valuesValidator;
    private final RecordFactory recordFactory;
    private final ModuleRecordRepository recordRepository;
    private final ModuleRecordMapper recordMapper;

    @Override
    @Transactional
    public ModuleRecordResponse create(String slug, RecordRequest request, Caller caller) {
        ModuleDefinition definition = definitionLookup.require(slug);
        Map<String, Object> values = valuesValidator.validate(definition, request.values());
        var record = recordFactory.initial(definition, values, caller.author());
        return recordMapper.toResponse(recordRepository.save(record));
    }
}
