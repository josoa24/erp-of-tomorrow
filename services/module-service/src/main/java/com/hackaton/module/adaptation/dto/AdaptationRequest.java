package com.hackaton.module.adaptation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdaptationRequest(@NotBlank @Size(max = 60) String moduleKey,
        @NotBlank @Size(max = 80) String adaptationKey) {
}
