package com.hackaton.stock.invoice.dto;

import com.hackaton.stock.invoice.entity.InvoiceEventType;
import java.time.Instant;

public record InvoiceEventResponse(
        InvoiceEventType type, Long actorId, String actorEmail, String actorRole, String detail, Instant createdAt) {
}
