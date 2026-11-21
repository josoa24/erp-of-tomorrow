package com.hackaton.stock.replenishment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.replenishment.config.ReplenishmentProperties;
import com.hackaton.stock.replenishment.dto.ForecastConfidence;
import com.hackaton.stock.replenishment.dto.LeadTimeSource;
import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReplenishmentCalculatorTest {

    private static final LeadTime SUPPLIER_4_DAYS = new LeadTime(4, LeadTimeSource.SUPPLIER, 3L);
    private static final LeadTime DEFAULT_7_DAYS = new LeadTime(7, LeadTimeSource.DEFAULT, null);

    private final ReplenishmentCalculator calculator = new ReplenishmentCalculator(new ReplenishmentProperties(7, 30));

    @Test
    void shouldComputeHandCheckedValues_whenHalfTheDaysHaveTwoOuts() {
        List<Long> outs = new ArrayList<>(Collections.nCopies(14, 2L));
        outs.addAll(Collections.nCopies(14, 0L));

        ReplenishmentForecast forecast = calculate(8, outs, true, SUPPLIER_4_DAYS);

        assertEquals(new BigDecimal("1.00"), forecast.averageDailyOut());
        assertEquals(new BigDecimal("1.00"), forecast.dailyOutStdDev());
        assertEquals(4, forecast.safetyStock());
        assertEquals(new BigDecimal("8.00"), forecast.reorderPoint());
        assertEquals(30, forecast.recommendedQuantity());
        assertEquals(new BigDecimal("8.0"), forecast.coverageDays());
        assertTrue(forecast.needsReorder());
        assertEquals(ForecastConfidence.OK, forecast.confidence());
        assertEquals(LeadTimeSource.SUPPLIER, forecast.leadTimeSource());
        assertEquals(3L, forecast.suggestedSupplierId());
        assertTrue(forecast.assumptions().get(2).contains("fournisseur 3"));
    }

    @Test
    void shouldNotReorder_whenStockIsAboveReorderPointWithDefaultLeadTime() {
        ReplenishmentForecast forecast = calculate(50, Collections.nCopies(28, 3L), true, DEFAULT_7_DAYS);

        assertEquals(0, forecast.safetyStock());
        assertEquals(new BigDecimal("21.00"), forecast.reorderPoint());
        assertEquals(61, forecast.recommendedQuantity());
        assertEquals(new BigDecimal("16.7"), forecast.coverageDays());
        assertFalse(forecast.needsReorder());
        assertEquals(7, forecast.leadTimeDays());
        assertTrue(forecast.assumptions().get(2).contains("valeur par défaut"));
    }

    @Test
    void shouldHaveNoCoverageAndNoReorder_whenThereIsNoConsumption() {
        ReplenishmentForecast forecast = calculate(0, Collections.nCopies(28, 0L), true, DEFAULT_7_DAYS);

        assertNull(forecast.coverageDays());
        assertFalse(forecast.needsReorder());
        assertEquals(0, forecast.recommendedQuantity());
    }

    @Test
    void shouldGiveNoQuantity_whenHistoryIsInsufficient() {
        ReplenishmentForecast forecast = calculate(0, Collections.nCopies(28, 5L), false, DEFAULT_7_DAYS);

        assertEquals(ForecastConfidence.INSUFFICIENT_DATA, forecast.confidence());
        assertNull(forecast.recommendedQuantity());
        assertFalse(forecast.needsReorder());
        assertTrue(forecast.assumptions().get(4).contains("Moins de 14 jours"));
    }

    @Test
    void shouldNeverRecommendNegativeQuantity_whenStockIsLarge() {
        ReplenishmentForecast forecast = calculate(1000, Collections.nCopies(28, 1L), true, SUPPLIER_4_DAYS);

        assertEquals(0, forecast.recommendedQuantity());
        assertFalse(forecast.needsReorder());
    }

    private ReplenishmentForecast calculate(int stock, List<Long> outs, boolean enough, LeadTime lead) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Riz");
        product.setQuantity(stock);
        return calculator.calculate(product, new ReplenishmentInput(outs, enough, lead));
    }
}
