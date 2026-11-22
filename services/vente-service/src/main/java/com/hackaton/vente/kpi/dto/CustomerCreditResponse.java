package com.hackaton.vente.kpi.dto;

import java.math.BigDecimal;

public record CustomerCreditResponse(Long customerId, BigDecimal outstanding, long overdueCount) {
}
