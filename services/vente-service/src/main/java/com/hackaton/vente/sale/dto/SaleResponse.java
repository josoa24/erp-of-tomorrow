package com.hackaton.vente.sale.dto;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SaleResponse(
        Long id,
        String number,
        Long customerId,
        String customerName,
        DocumentStatus status,
        PaymentStatus paymentStatus,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        PaymentMethod paymentMethod,
        Integer creditDays,
        LocalDate dueDate,
        boolean toDeliver,
        List<SaleLineResponse> lines,
        Instant createdAt,
        Instant validatedAt) {
}
