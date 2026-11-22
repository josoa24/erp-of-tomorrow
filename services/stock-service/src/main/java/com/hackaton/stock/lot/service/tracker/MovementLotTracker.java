package com.hackaton.stock.lot.service.tracker;

import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;

public interface MovementLotTracker {

    void check(MovementRequest request);

    void track(Product product, StockMovement movement, MovementRequest request);
}
