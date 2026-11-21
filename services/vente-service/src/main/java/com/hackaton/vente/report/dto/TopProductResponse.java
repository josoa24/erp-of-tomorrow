package com.hackaton.vente.report.dto;

import java.math.BigDecimal;

public record TopProductResponse(Long productId, String productName, long totalQuantity, BigDecimal totalRevenue) {
}
