package com.hackaton.rh.leave.controller;

import com.hackaton.rh.leave.dto.LeaveResponse;
import com.hackaton.rh.leave.entity.LeaveStatus;
import java.time.Instant;
import java.time.LocalDate;

/**
 * Données de test des controllers congés.
 */
final class LeaveFixtures {

    static final String VALID_BODY = """
            {"employeeId":7,"startDate":"2026-12-01","endDate":"2026-12-05","reason":"Vacances"}
            """;

    private LeaveFixtures() {
    }

    static LeaveResponse leave(LeaveStatus status) {
        return new LeaveResponse(1L, 7L, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 5), 5, "Vacances",
                status, Instant.EPOCH, status == LeaveStatus.DRAFT ? null : Instant.EPOCH);
    }
}
