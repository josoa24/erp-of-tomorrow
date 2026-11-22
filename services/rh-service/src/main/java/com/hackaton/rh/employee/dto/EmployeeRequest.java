package com.hackaton.rh.employee.dto;

import com.hackaton.rh.employee.entity.ContractType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record EmployeeRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        String position,
        @NotNull Long departmentId,
        @NotNull @PastOrPresent LocalDate hireDate,
        @NotNull @DecimalMin("0.0") BigDecimal baseSalary,
        @Positive Long userId,
        ContractType contractType) {
}
