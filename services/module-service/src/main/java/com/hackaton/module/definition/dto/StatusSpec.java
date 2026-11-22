package com.hackaton.module.definition.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hackaton.module.definition.entity.StatusTone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record StatusSpec(
        @NotBlank @Pattern(regexp = SpecPatterns.KEY, message = SpecPatterns.KEY_MESSAGE) String key,
        @NotBlank @Size(max = 60) String label,
        @NotNull StatusTone tone,
        @JsonProperty("final") boolean finalStatus) {
}
