package com.hackaton.stock.supplier.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SupplierRequest(
        @NotBlank String name,
        String phone,
        @Email String email,
        String address,
        String paymentTerms,
        @Min(0) Integer usualDeliveryDays,
        @Size(max = 64) String bankAccount,
        @Size(max = 80) String city,
        @Size(max = 20) String nif,
        @Size(max = 30) String stat) {
}
