package com.hackaton.rh.advance.dto;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

public record AdvanceResponse(
        Long id,
        Long employeeId,
        String employeeName,
        YearMonth month,
        BigDecimal amount,
        String reason,
        AdvanceStatus status,
        Instant createdAt,
        Instant decidedAt,
        Long decidedBy) {
}
