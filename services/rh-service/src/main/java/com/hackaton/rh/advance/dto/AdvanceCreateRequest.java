package com.hackaton.rh.advance.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AdvanceCreateRequest(
        @NotNull Long employeeId,
        @NotNull @Positive @Digits(integer = 12, fraction = 2) BigDecimal amount,
        @Size(max = 500) String reason) {
}
