package com.hackaton.stock.invoice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record InvoiceLineRequest(
        Long productId,
        @NotBlank @Size(max = 255) String description,
        @Min(1) int quantity,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice) {

    public BigDecimal amount() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
