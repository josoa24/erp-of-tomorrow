package com.hackaton.stock.movement.service.creator;

import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;

public interface StockMovementCreator {

    MovementResponse record(Long productId, MovementRequest request);
}
