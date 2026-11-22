package com.hackaton.module.support;

import com.hackaton.module.definition.entity.ModuleDefinition;
import com.hackaton.module.record.dto.ModuleRecordResponse;
import com.hackaton.module.record.entity.ModuleRecord;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RecordFixtures {

    private RecordFixtures() {
    }

    public static ModuleRecord record(ModuleDefinition definition, long id, String status, Map<String, Object> values,
            Instant createdAt) {
        ModuleRecord record = new ModuleRecord();
        record.setId(id);
        record.setDefinition(definition);
        record.setStatus(status);
        record.setValues(new LinkedHashMap<>(values));
        record.setCreatedBy(GatewayHeaders.SELLER_EMAIL);
        record.setCreatedAt(createdAt);
        record.setUpdatedAt(createdAt);
        return record;
    }

    public static Map<String, Object> ref(long id, String label) {
        Map<String, Object> value = new LinkedHashMap<>();
        value.put("id", id);
        value.put("label", label);
        return value;
    }

    public static ModuleRecordResponse response(long id, String status) {
        return new ModuleRecordResponse(id, Map.of("quartier", "Isotry"), status, GatewayHeaders.SELLER_EMAIL,
                TestClocks.NOW, TestClocks.NOW);
    }
}
