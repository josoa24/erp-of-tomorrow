package com.hackaton.stock.status.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.stock.product.dto.StockRisk;
import org.junit.jupiter.api.Test;

class StockStatusLabelTest {

    @Test
    void shouldBeRuptureRisk_whenRiskIsCriticalEvenAboveThreshold() {
        assertEquals(StockStatusLabel.RUPTURE_RISK, StockStatusLabel.of(StockRisk.CRITICAL, false));
    }

    @Test
    void shouldBeBelowThreshold_whenLowStockWithoutCriticalRisk() {
        assertEquals(StockStatusLabel.BELOW_THRESHOLD, StockStatusLabel.of(StockRisk.WARNING, true));
    }

    @Test
    void shouldBeInStock_whenAboveThresholdWithoutCriticalRisk() {
        assertEquals(StockStatusLabel.IN_STOCK, StockStatusLabel.of(StockRisk.WARNING, false));
    }
}
