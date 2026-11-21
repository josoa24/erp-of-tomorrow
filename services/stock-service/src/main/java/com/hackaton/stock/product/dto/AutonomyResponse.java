package com.hackaton.stock.product.dto;

import java.math.BigDecimal;

public record AutonomyResponse(
        Long productId,
        String productName,
        int currentStock,
        int analysisDays,
        BigDecimal averageDailyOut,
        BigDecimal daysRemaining,
        StockRisk risk) {
}
