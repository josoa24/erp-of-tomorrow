package com.hackaton.stock.invoice.dto;

import java.math.BigDecimal;

public record InvoiceLineResponse(Long productId, String description, int quantity, BigDecimal unitPrice) {
}
