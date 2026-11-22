package com.hackaton.stock.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductCreateRequest(
        @NotBlank String name,
        @NotBlank String sku,
        @NotBlank String unit,
        @Min(0) int quantity,
        @Min(0) int minThreshold,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
        @Size(max = 80) String category,
        @DecimalMin("0.0") BigDecimal purchasePrice) {
}
