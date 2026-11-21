package com.hackaton.rh.employee.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String position,
        Long departmentId,
        LocalDate hireDate,
        BigDecimal baseSalary,
        int leaveBalance,
        Long userId,
        Instant createdAt) {
}
