package com.hackaton.vente.customer.dto;

import com.hackaton.vente.customer.entity.CustomerType;
import java.math.BigDecimal;
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
        Instant createdAt,
        String nif,
        String stat,
        BigDecimal creditLimit) {
}
