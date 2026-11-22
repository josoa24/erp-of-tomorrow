package com.hackaton.rh.leave.dto;

import com.hackaton.rh.leave.entity.LeaveStatus;
import java.time.Instant;
import java.time.LocalDate;

public record LeaveResponse(
        Long id,
        Long employeeId,
        String employeeName,
        LocalDate startDate,
        LocalDate endDate,
        int days,
        String reason,
        LeaveStatus status,
        Instant createdAt,
        Instant decidedAt,
        Long decidedBy) {
}
