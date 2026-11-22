package com.hackaton.vente.stock.client;

import java.util.List;

public interface StockClient {

    StockProduct getProduct(Long productId);

    List<StockCatalogProduct> searchProducts(String search);

    void recordMovement(Long productId, MovementKind kind, int quantity, String reason);
}
