package com.hackaton.stock.supplier.dto;

import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record RecentOrder(Long id, OrderStatus status, BigDecimal totalAmount, Instant createdAt) {
}
