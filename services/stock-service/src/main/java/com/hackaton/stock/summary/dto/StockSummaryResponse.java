package com.hackaton.stock.summary.dto;

import java.math.BigDecimal;

public record StockSummaryResponse(
        long totalProducts,
        long lowStockProducts,
        BigDecimal totalStockValue,
        BigDecimal availabilityRate,
        long criticalLowStock,
        BigDecimal totalPurchaseValue,
        long productsWithoutPurchasePrice,
        long expiringSoon) {
}
