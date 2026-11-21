package com.hackaton.stock.movement.dto;

import com.hackaton.stock.movement.entity.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MovementRequest(
        @NotNull MovementType type,
        @Positive int quantity,
        String reason) {
}
