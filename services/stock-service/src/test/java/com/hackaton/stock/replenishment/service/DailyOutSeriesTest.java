package com.hackaton.stock.replenishment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.stock.movement.entity.StockMovement;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class DailyOutSeriesTest {

    private static final LocalDate FIRST_DAY = LocalDate.parse("2026-11-01");

    @Test
    void shouldSumOutsPerCalendarDay_andIgnoreDaysOutsideTheWindow() {
        List<StockMovement> outs = List.of(
                out("2026-11-01T08:00:00Z", 2),
                out("2026-11-01T18:00:00Z", 3),
                out("2026-11-03T10:00:00Z", 4),
                out("2026-10-31T23:59:00Z", 9),
                out("2026-11-05T00:00:00Z", 9));

        List<Long> series = DailyOutSeries.of(outs, FIRST_DAY, 4);

        assertEquals(List.of(5L, 0L, 4L, 0L), series);
    }

    private static StockMovement out(String at, int quantity) {
        StockMovement movement = new StockMovement();
        movement.setCreatedAt(Instant.parse(at));
        movement.setQuantity(quantity);
        return movement;
    }
}
