package com.hackaton.vente.customer.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record RecentSale(Long id, BigDecimal totalAmount, Instant validatedAt) {
}
