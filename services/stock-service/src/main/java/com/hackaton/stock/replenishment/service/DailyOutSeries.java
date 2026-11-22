package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.movement.entity.StockMovement;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/** Répartit des sorties par jour calendaire (UTC) ; les jours sans sortie valent 0. */
public final class DailyOutSeries {

    private DailyOutSeries() {
    }

    public static List<Long> of(List<StockMovement> outs, LocalDate firstDay, int days) {
        Long[] totals = new Long[days];
        Arrays.fill(totals, 0L);
        for (StockMovement out : outs) {
            LocalDate day = LocalDate.ofInstant(out.getCreatedAt(), ZoneOffset.UTC);
            long index = ChronoUnit.DAYS.between(firstDay, day);
            if (index >= 0 && index < days) {
                totals[(int) index] += out.getQuantity();
            }
        }
        return List.of(totals);
    }
}
