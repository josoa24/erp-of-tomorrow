package com.hackaton.rh.employee.dto;

import com.hackaton.rh.employee.entity.ContractType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String position,
        ContractType contractType,
        Long departmentId,
        String departmentName,
        LocalDate hireDate,
        BigDecimal baseSalary,
        int leaveBalance,
        Long userId,
        Instant createdAt,
        boolean onLeaveToday,
        EmployeeStatus status,
        BigDecimal monthAdvance,
        boolean monthAdvancePending,
        BigDecimal advanceCap) {
}
