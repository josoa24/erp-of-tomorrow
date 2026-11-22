package com.hackaton.rh.advance.controller;

import com.hackaton.rh.advance.dto.AdvanceResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;

final class AdvanceFixtures {

    static final String VALID_BODY = """
            {"employeeId":7,"amount":80000,"reason":"Frais de scolarité"}
            """;

    private AdvanceFixtures() {
    }

    static AdvanceResponse advance(AdvanceStatus status) {
        boolean decided = status != AdvanceStatus.DRAFT;
        return new AdvanceResponse(3L, 7L, "Nirina Rabe", YearMonth.of(2026, 11), new BigDecimal("80000"),
                "Frais de scolarité", status, Instant.EPOCH, decided ? Instant.EPOCH : null, decided ? 1L : null);
    }
}
