package com.hackaton.stock.purchaseorder.dto;

import java.math.BigDecimal;

public record PurchaseOrderLineResponse(
        Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}
