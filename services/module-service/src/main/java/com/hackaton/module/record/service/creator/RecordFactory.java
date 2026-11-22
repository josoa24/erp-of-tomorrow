package com.hackaton.module.record.service.creator;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.record.entity.ModuleRecord;
import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecordFactory {

    private final Clock clock;

    public ModuleRecord initial(ModuleDefinition definition, Map<String, Object> values, String author) {
        Instant now = Instant.now(clock);
        ModuleRecord record = new ModuleRecord();
        record.setDefinition(definition);
        record.setValues(new LinkedHashMap<>(values));
        record.setStatus(definition.getStatuses().get(0).getKey());
        record.setCreatedBy(author);
        record.setCreatedAt(now);
        record.setUpdatedAt(now);
        return record;
    }
}
