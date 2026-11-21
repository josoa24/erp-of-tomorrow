package com.hackaton.rh.attendance.dto;

import jakarta.validation.constraints.NotNull;

public record AttendanceRequest(@NotNull Long employeeId) {
}
