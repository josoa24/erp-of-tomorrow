package com.hackaton.vente.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public final class LocalDays {

    private LocalDays() {
    }

    public static LocalDate dayOf(Instant instant, ZoneId zone) {
        return instant.atZone(zone).toLocalDate();
    }

    public static Instant startOf(LocalDate day, ZoneId zone) {
        return day.atStartOfDay(zone).toInstant();
    }

    public static boolean isWithin(Instant instant, LocalDate from, LocalDate to, ZoneId zone) {
        LocalDate day = dayOf(instant, zone);
        return (from == null || !day.isBefore(from)) && (to == null || !day.isAfter(to));
    }
}
