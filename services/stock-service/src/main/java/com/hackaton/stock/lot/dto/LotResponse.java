package com.hackaton.stock.lot.dto;

import java.time.Instant;
import java.time.LocalDate;

public record LotResponse(
        String lotNumber,
        LocalDate expiryDate,
        int quantity,
        long remaining,
        Instant receivedAt) {
}
