package com.hackaton.vente.sale.dto;

import com.hackaton.vente.common.status.DocumentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record SaleResponse(
        Long id,
        Long customerId,
        DocumentStatus status,
        BigDecimal totalAmount,
        List<SaleLineResponse> lines,
        Instant createdAt,
        Instant validatedAt) {
}
