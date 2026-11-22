package com.hackaton.module.salesimport.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.module.support.TestClocks;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class WeekWindowTest {

    private final WeekWindow week = WeekWindow.current(TestClocks.SUNDAY_NOON);

    @Test
    void shouldSpanMondayToSunday_whenTodayIsSunday() {
        assertEquals(LocalDate.parse("2026-11-16"), week.monday());
        assertEquals(LocalDate.parse("2026-11-22"), week.sunday());
    }

    @Test
    void shouldStartOnToday_whenTodayIsMonday() {
        Clock monday = Clock.fixed(Instant.parse("2026-11-16T05:00:00Z"), TestClocks.ANTANANARIVO);

        assertEquals(LocalDate.parse("2026-11-16"), WeekWindow.current(monday).monday());
    }

    @Test
    void shouldUseAntananarivoDay_atWeekBoundaries() {
        assertTrue(week.contains(Instant.parse("2026-11-15T22:00:00Z")));
        assertFalse(week.contains(Instant.parse("2026-11-15T20:59:00Z")));
        assertTrue(week.contains(Instant.parse("2026-11-22T20:59:00Z")));
        assertFalse(week.contains(Instant.parse("2026-11-22T21:00:00Z")));
    }

    @Test
    void shouldExcludeMissingInstant() {
        assertFalse(week.contains(null));
    }
}
