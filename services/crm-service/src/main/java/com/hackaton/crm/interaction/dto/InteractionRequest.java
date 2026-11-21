package com.hackaton.crm.interaction.dto;

import com.hackaton.crm.interaction.entity.InteractionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.Instant;

public record InteractionRequest(
        @NotNull InteractionType type,
        @PastOrPresent Instant occurredAt,
        @NotBlank @Size(max = 2000) String summary) {
}
