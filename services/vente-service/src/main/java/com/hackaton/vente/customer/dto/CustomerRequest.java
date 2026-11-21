package com.hackaton.vente.customer.dto;

import com.hackaton.vente.customer.entity.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank String name,
        String phone,
        @Email String email,
        String address,
        String city,
        CustomerType type,
        @Size(max = 2000) String notes) {
}
