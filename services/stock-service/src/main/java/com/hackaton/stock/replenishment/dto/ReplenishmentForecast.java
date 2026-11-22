package com.hackaton.stock.replenishment.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReplenishmentForecast(
        Long productId,
        String productName,
        int currentStock,
        int analysisDays,
        BigDecimal averageDailyOut,
        BigDecimal dailyOutStdDev,
        int leadTimeDays,
        LeadTimeSource leadTimeSource,
        Long suggestedSupplierId,
        int safetyStock,
        BigDecimal reorderPoint,
        Integer recommendedQuantity,
        BigDecimal coverageDays,
        boolean needsReorder,
        ForecastConfidence confidence,
        List<String> assumptions) {
}
