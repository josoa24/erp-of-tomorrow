package com.hackaton.vente.customer.dto;

import com.hackaton.vente.customer.entity.CustomerType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CustomerRequest(
        @NotBlank String name,
        String phone,
        @Email String email,
        String address,
        String city,
        CustomerType type,
        @Size(max = 2000) String notes,
        @Size(max = 20) String nif,
        @Size(max = 30) String stat,
        @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal creditLimit) {
}
