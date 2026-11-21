package com.hackaton.rh.attendance.dto;

import java.time.Instant;
import java.time.LocalDate;

public record AttendanceResponse(
        Long id, Long employeeId, LocalDate workDate, Instant checkIn, Instant checkOut, Long workedMinutes) {
}
