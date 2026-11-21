package com.hackaton.vente.stock.client;

public interface StockClient {

    StockProduct getProduct(Long productId);

    void recordMovement(Long productId, MovementKind kind, int quantity, String reason);
}
