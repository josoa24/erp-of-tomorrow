package com.hackaton.stock.lot.service.allocator;

import com.hackaton.stock.movement.entity.StockMovement;

public interface StockLotAllocator {

    void allocate(StockMovement exit);
}
