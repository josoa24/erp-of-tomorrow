package com.hackaton.stock.movement.dto;

import com.hackaton.stock.movement.entity.MovementType;
import java.time.Instant;

public record MovementResponse(
        Long id,
        Long productId,
        MovementType type,
        int quantity,
        String reason,
        int stockAfter,
        Instant createdAt) {
}
