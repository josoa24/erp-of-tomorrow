package com.hackaton.rh.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.rh.employee.dto.EmployeeRequest;
import jakarta.validation.Configuration;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class ClockConfigTest {

    private static final ZoneId ANTANANARIVO = ZoneId.of("Indian/Antananarivo");
    private static final Clock UTC_EVENING = Clock.fixed(Instant.parse("2026-11-21T22:00:00Z"), ANTANANARIVO);

    private final ClockConfig config = new ClockConfig();

    @Test
    void shouldUseConfiguredZone_whenBuildingClock() {
        Clock clock = config.clock("Indian/Antananarivo");

        assertEquals(ANTANANARIVO, clock.getZone());
    }

    @Test
    void shouldAcceptLocalToday_whenUtcDateIsStillYesterday() {
        Validator validator = validatorWith(UTC_EVENING);

        assertEquals(0, validator.validate(hiredOn(LocalDate.of(2026, 11, 22))).size());
    }

    @Test
    void shouldRefuseLocalTomorrow_whenValidatingHireDate() {
        Validator validator = validatorWith(UTC_EVENING);

        assertEquals(1, validator.validate(hiredOn(LocalDate.of(2026, 11, 23))).size());
    }

    private Validator validatorWith(Clock clock) {
        Configuration<?> configuration = Validation.byDefaultProvider().configure();
        config.localValidationClock(clock).customize(configuration);
        return configuration.buildValidatorFactory().getValidator();
    }

    private EmployeeRequest hiredOn(LocalDate hireDate) {
        return new EmployeeRequest("Hery", "Rabe", "hery.rabe@erp.local", "Chauffeur", 1L, hireDate,
                new BigDecimal("800000"), null, null);
    }
}
