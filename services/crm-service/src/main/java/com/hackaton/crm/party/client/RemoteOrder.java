package com.hackaton.crm.party.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteOrder(
        Long id, String status, BigDecimal totalAmount, Instant validatedAt, Instant receivedAt) {
}
