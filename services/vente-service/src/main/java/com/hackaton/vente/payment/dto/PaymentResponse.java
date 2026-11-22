package com.hackaton.vente.payment.dto;

import com.hackaton.vente.sale.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id, Long saleId, BigDecimal amount, PaymentMethod method, Instant paidAt, String reference,
        Long recordedBy) {
}
