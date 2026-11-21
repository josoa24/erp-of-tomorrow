package com.hackaton.stock.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record SupplierRequest(
        @NotBlank String name,
        String phone,
        @Email String email,
        String address,
        String paymentTerms,
        @Min(0) Integer usualDeliveryDays) {
}
