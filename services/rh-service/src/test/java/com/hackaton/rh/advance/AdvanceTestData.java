package com.hackaton.rh.advance;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.entity.SalaryAdvance;
import com.hackaton.rh.employee.entity.Employee;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class AdvanceTestData {

    public static final ZoneId ANTANANARIVO = ZoneId.of("Indian/Antananarivo");
    public static final LocalDate NOVEMBER = LocalDate.of(2026, 11, 1);
    public static final LocalDate DECEMBER = LocalDate.of(2026, 12, 1);

    private AdvanceTestData() {
    }

    public static Clock clockAt(String instant) {
        return Clock.fixed(Instant.parse(instant), ANTANANARIVO);
    }

    public static Employee toky() {
        Employee employee = new Employee();
        employee.setId(7L);
        employee.setFirstName("Toky");
        employee.setLastName("Rakoto");
        employee.setBaseSalary(new BigDecimal("800000"));
        return employee;
    }

    public static SalaryAdvance advance(Long id, Long employeeId, LocalDate month, String amount, AdvanceStatus status) {
        SalaryAdvance advance = new SalaryAdvance();
        advance.setId(id);
        advance.setEmployeeId(employeeId);
        advance.setMonth(month);
        advance.setAmount(new BigDecimal(amount));
        advance.setStatus(status);
        return advance;
    }
}
