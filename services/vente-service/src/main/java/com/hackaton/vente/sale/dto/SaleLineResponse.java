package com.hackaton.vente.sale.dto;

import java.math.BigDecimal;

public record SaleLineResponse(
        Long productId, String productName, int quantity, BigDecimal unitPrice, BigDecimal lineTotal) {
}
