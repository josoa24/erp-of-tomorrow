package com.hackaton.module.reference.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteSale(
        Long id,
        Long customerId,
        String status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant validatedAt,
        Boolean toDeliver) {
}
