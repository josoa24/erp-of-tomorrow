package com.hackaton.crm.contact.service.summary;

import java.time.LocalDate;

public final class QuarterStart {

    private static final int MONTHS_PER_QUARTER = 3;

    private QuarterStart() {
    }

    public static LocalDate of(LocalDate day) {
        int firstMonth = ((day.getMonthValue() - 1) / MONTHS_PER_QUARTER) * MONTHS_PER_QUARTER + 1;
        return LocalDate.of(day.getYear(), firstMonth, 1);
    }
}
