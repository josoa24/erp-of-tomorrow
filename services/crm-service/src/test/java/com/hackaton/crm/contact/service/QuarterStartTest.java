package com.hackaton.crm.contact.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.crm.contact.service.summary.QuarterStart;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class QuarterStartTest {

    @Test
    void shouldReturnOctoberFirst_whenDayIsInTheFourthQuarter() {
        assertEquals(LocalDate.of(2026, 10, 1), QuarterStart.of(LocalDate.of(2026, 11, 22)));
        assertEquals(LocalDate.of(2026, 10, 1), QuarterStart.of(LocalDate.of(2026, 12, 31)));
    }

    @Test
    void shouldReturnJanuaryFirst_whenDayIsInTheFirstQuarter() {
        assertEquals(LocalDate.of(2026, 1, 1), QuarterStart.of(LocalDate.of(2026, 1, 1)));
        assertEquals(LocalDate.of(2026, 1, 1), QuarterStart.of(LocalDate.of(2026, 3, 31)));
    }

    @Test
    void shouldReturnAprilFirst_whenDayStartsTheSecondQuarter() {
        assertEquals(LocalDate.of(2026, 4, 1), QuarterStart.of(LocalDate.of(2026, 4, 1)));
    }
}
