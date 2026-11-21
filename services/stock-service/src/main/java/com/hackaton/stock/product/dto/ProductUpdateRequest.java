package com.hackaton.stock.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ProductUpdateRequest(
        @NotBlank String name,
        @NotBlank String unit,
        @Min(0) int minThreshold,
        @NotNull @DecimalMin("0.0") BigDecimal unitPrice) {
}
