package com.hackaton.stock.invoice.dto;

import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.shield.ShieldVerdict;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record SupplierInvoiceResponse(
        Long id,
        Long supplierId,
        String invoiceNumber,
        LocalDate invoiceDate,
        Long purchaseOrderId,
        String bankAccountLast4,
        List<InvoiceLineResponse> lines,
        BigDecimal subtotal,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        InvoiceStatus status,
        ShieldVerdict verdict,
        List<ShieldAlert> alerts,
        Long createdBy,
        Instant createdAt,
        Long decidedBy,
        Instant decidedAt,
        String decisionNote) {
}
