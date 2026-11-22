package com.hackaton.module.definition.dto;

import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.definition.entity.ReferenceTarget;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ColumnSpec(
        @NotBlank @Pattern(regexp = SpecPatterns.KEY, message = SpecPatterns.KEY_MESSAGE) String key,
        @NotBlank @Size(max = 60) String label,
        @NotNull ColumnType type,
        boolean required,
        @Size(max = 20) List<@NotBlank @Size(max = 60) String> choices,
        ReferenceTarget reference,
        Boolean listed) {

    public ColumnSpec(String key, String label, ColumnType type, boolean required, List<String> choices,
                      ReferenceTarget reference) {
        this(key, label, type, required, choices, reference, null);
    }

    public boolean shownInList() {
        return listed == null || listed;
    }
}
