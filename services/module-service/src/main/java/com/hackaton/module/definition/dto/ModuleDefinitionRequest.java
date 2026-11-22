package com.hackaton.module.definition.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ModuleDefinitionRequest(
        @NotBlank @Size(min = 2, max = 40) String name,
        @NotBlank @Pattern(regexp = SpecPatterns.MODULE_ICON, message = SpecPatterns.MODULE_ICON_MESSAGE) String icon,
        @Size(max = 280) String description,
        @NotNull @Size(min = 1, max = 12) List<@NotNull @Valid ColumnSpec> columns,
        @NotNull @Size(min = 2, max = 6) List<@NotNull @Valid StatusSpec> statuses,
        @Size(max = 4) List<@NotNull @Valid IndicatorSpec> indicators) {
}
