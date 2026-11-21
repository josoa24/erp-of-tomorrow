package com.hackaton.vente.report.dto;

import java.math.BigDecimal;
import java.util.List;

public record SalesSummaryResponse(
        long salesCount, BigDecimal totalRevenue, BigDecimal averageBasket, List<DailyRevenue> daily) {
}
