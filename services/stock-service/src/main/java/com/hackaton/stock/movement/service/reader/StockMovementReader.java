package com.hackaton.stock.movement.service.reader;

import com.hackaton.stock.movement.dto.MovementResponse;
import java.util.List;

public interface StockMovementReader {

    List<MovementResponse> findByProductId(Long productId);
}
