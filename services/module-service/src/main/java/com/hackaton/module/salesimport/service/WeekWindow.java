package com.hackaton.module.salesimport.service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public record WeekWindow(LocalDate monday, LocalDate sunday, ZoneId zone) {

    public static WeekWindow current(Clock clock) {
        LocalDate monday = LocalDate.now(clock).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return new WeekWindow(monday, monday.plusDays(6), clock.getZone());
    }

    public boolean contains(Instant instant) {
        if (instant == null) {
            return false;
        }
        LocalDate day = instant.atZone(zone).toLocalDate();
        return !day.isBefore(monday) && !day.isAfter(sunday);
    }
}
