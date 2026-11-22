package com.hackaton.module.insight.dto;

import com.hackaton.module.definition.entity.IndicatorKind;
import java.math.BigDecimal;
import java.util.List;

public record IndicatorValueResponse(
        String key,
        String label,
        String icon,
        IndicatorKind kind,
        BigDecimal value,
        Long total,
        boolean money,
        List<String> detail) {
}
