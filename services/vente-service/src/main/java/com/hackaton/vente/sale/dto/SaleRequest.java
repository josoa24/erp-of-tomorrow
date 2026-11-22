package com.hackaton.vente.sale.dto;

import com.hackaton.vente.sale.entity.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record SaleRequest(
        Long customerId,
        @NotEmpty @Valid List<SaleLineRequest> lines,
        PaymentMethod paymentMethod,
        @Min(1) @Max(365) Integer creditDays,
        Boolean toDeliver) {
}
