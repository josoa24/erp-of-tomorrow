package com.hackaton.module.record.dto;

import java.time.Instant;
import java.util.Map;

public record ModuleRecordResponse(
        Long id,
        Map<String, Object> values,
        String status,
        String createdBy,
        Instant createdAt,
        Instant updatedAt) {
}
