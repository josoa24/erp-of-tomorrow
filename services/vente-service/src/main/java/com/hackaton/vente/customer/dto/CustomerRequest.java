package com.hackaton.vente.customer.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(@NotBlank String name, String phone, String address) {
}
