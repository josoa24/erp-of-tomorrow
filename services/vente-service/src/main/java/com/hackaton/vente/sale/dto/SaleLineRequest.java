package com.hackaton.vente.sale.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaleLineRequest(@NotNull Long productId, @Positive int quantity) {
}
