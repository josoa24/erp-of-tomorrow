package com.hackaton.stock.status.dto;

import com.hackaton.stock.product.dto.StockRisk;
import java.math.BigDecimal;

public record StockStatusItem(
        Long productId,
        String productName,
        String sku,
        String unit,
        int quantity,
        int minThreshold,
        boolean lowStock,
        BigDecimal averageDailyOut,
        BigDecimal daysRemaining,
        StockRisk risk) {
}
