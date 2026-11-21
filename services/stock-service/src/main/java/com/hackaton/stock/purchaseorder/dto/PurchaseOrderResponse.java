package com.hackaton.stock.purchaseorder.dto;

import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PurchaseOrderResponse(
        Long id,
        Long supplierId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<PurchaseOrderLineResponse> lines,
        Instant createdAt,
        Instant validatedAt,
        Instant receivedAt) {
}
