package com.hackaton.vente.customer.dto;

import com.hackaton.vente.report.dto.TopProductResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CustomerProfileResponse(
        CustomerResponse customer,
        long salesCount,
        BigDecimal totalPurchased,
        BigDecimal averageBasket,
        Instant lastPurchaseAt,
        List<TopProductResponse> topProducts,
        List<RecentSale> recentSales) {
}
