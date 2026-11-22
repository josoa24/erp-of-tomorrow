package com.hackaton.module.definition.dto;

import java.time.Instant;
import java.util.List;

public record ModuleDefinitionResponse(
        Long id,
        String slug,
        String name,
        String icon,
        String description,
        List<ColumnSpec> columns,
        List<StatusSpec> statuses,
        List<IndicatorSpec> indicators,
        String createdBy,
        Instant createdAt) {
}
