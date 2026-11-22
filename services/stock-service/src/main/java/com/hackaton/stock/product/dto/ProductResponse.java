package com.hackaton.stock.product.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String sku,
        String unit,
        int quantity,
        int minThreshold,
        BigDecimal unitPrice,
        boolean lowStock,
        Instant createdAt,
        String category,
        BigDecimal purchasePrice) {
}
