package com.hackaton.crm.party.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RemoteCustomer(
        Long id,
        String name,
        String phone,
        String email,
        String city,
        String type,
        String nif,
        String stat,
        BigDecimal creditLimit,
        Instant createdAt) {
}
