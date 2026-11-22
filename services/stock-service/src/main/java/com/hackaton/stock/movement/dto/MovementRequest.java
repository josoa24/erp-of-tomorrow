package com.hackaton.stock.movement.dto;

import com.hackaton.stock.movement.entity.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MovementRequest(
        @NotNull MovementType type,
        @Positive int quantity,
        String reason,
        @Size(max = 64) String lotNumber,
        LocalDate expiryDate) {

    public MovementRequest(MovementType type, int quantity, String reason) {
        this(type, quantity, reason, null, null);
    }
}
