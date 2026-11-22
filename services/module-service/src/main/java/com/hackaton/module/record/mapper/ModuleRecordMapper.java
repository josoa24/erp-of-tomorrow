package com.hackaton.module.record.mapper;

import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import java.util.LinkedHashMap;
import org.springframework.stereotype.Component;

@Component
public class ModuleRecordMapper {

    public ModuleRecordResponse toResponse(ModuleRecord record) {
        return new ModuleRecordResponse(record.getId(), new LinkedHashMap<>(record.getValues()), record.getStatus(),
                record.getCreatedBy(), record.getCreatedAt(), record.getUpdatedAt());
    }
}
