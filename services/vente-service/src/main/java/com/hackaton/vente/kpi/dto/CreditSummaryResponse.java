package com.hackaton.vente.kpi.dto;

import java.math.BigDecimal;

public record CreditSummaryResponse(BigDecimal outstanding, long customers, long overdueCustomers) {
}
