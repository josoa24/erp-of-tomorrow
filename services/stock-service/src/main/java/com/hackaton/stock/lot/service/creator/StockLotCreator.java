package com.hackaton.stock.lot.service.creator;

import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import java.time.LocalDate;

public interface StockLotCreator {

    void receive(Product product, StockMovement movement, String lotNumber, LocalDate expiryDate);
}
