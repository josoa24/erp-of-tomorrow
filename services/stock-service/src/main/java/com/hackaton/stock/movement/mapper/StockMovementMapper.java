package com.hackaton.stock.movement.mapper;

import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.StockMovement;
import org.springframework.stereotype.Component;

@Component
public class StockMovementMapper {

    public MovementResponse toResponse(StockMovement movement) {
        return new MovementResponse(
                movement.getId(),
                movement.getProductId(),
                movement.getType(),
                movement.getQuantity(),
                movement.getReason(),
                movement.getStockAfter(),
                movement.getCreatedAt());
    }
}
