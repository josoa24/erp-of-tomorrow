package com.hackaton.stock.purchaseorder.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record PurchaseOrderLineRequest(
        @NotNull Long productId,
        @Positive int quantity,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice) {
}
