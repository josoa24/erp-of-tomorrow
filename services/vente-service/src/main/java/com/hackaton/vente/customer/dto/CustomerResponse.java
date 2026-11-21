package com.hackaton.vente.customer.dto;

import java.time.Instant;

public record CustomerResponse(Long id, String name, String phone, String address, Instant createdAt) {
}
