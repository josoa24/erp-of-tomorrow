package com.hackaton.vente.customer.dto;

import com.hackaton.vente.customer.entity.CustomerType;
import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email,
        String address,
        String city,
        CustomerType type,
        String notes,
        Instant createdAt) {
}
