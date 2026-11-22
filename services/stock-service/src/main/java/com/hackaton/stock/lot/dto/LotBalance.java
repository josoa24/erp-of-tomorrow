package com.hackaton.stock.lot.dto;

import java.time.Instant;
import java.time.LocalDate;

public record LotBalance(
        Long lotId,
        Long productId,
        String lotNumber,
        LocalDate expiryDate,
        int quantity,
        long allocated,
        Instant receivedAt) {

    public long remaining() {
        return quantity - allocated;
    }
}
