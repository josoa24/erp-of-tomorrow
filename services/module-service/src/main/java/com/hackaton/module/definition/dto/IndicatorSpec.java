package com.hackaton.module.definition.dto;

import com.hackaton.module.definition.entity.IndicatorKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record IndicatorSpec(
        @NotBlank @Pattern(regexp = SpecPatterns.KEY, message = SpecPatterns.KEY_MESSAGE) String key,
        @NotBlank @Size(max = 60) String label,
        @NotBlank @Pattern(regexp = SpecPatterns.ICON, message = SpecPatterns.ICON_MESSAGE) String icon,
        @NotNull IndicatorKind kind,
        @Size(max = 6) List<@NotBlank String> statuses,
        String column,
        String todayColumn,
        String detailColumn) {
}
