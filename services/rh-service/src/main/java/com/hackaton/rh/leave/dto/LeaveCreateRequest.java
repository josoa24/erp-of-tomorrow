package com.hackaton.rh.leave.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record LeaveCreateRequest(
        @NotNull Long employeeId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        String reason) {
}
