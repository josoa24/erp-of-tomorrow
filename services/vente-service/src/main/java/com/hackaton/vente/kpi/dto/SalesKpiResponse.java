package com.hackaton.vente.kpi.dto;

import java.math.BigDecimal;

public record SalesKpiResponse(
        BigDecimal monthRevenue,
        BigDecimal previousMonthRevenue,
        long todaySalesCount,
        BigDecimal todayRevenue,
        BigDecimal todayCollected,
        BigDecimal sameWeekdayLastWeekRevenue,
        BigDecimal averageBasket,
        BigDecimal previousMonthAverageBasket) {
}
