package com.hackaton.stock.status.dto;

import static com.hackaton.stock.status.StatusItemFixtures.item;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.product.dto.StockRisk;
import org.junit.jupiter.api.Test;

class StockStatusQueryTest {

    private final StockStatusItem oil = item(1L, "Huile 5 L", "Épicerie", StockRisk.CRITICAL, true, 23);
    private final StockStatusItem candles = item(2L, "Bougies", null, StockRisk.OK, false, null);

    @Test
    void shouldMatchEverything_whenNoCriterionIsGiven() {
        assertTrue(StockStatusQuery.everything().matches(oil));
        assertTrue(StockStatusQuery.everything().matches(candles));
    }

    @Test
    void shouldMatchOnlyRequestedRisk() {
        StockStatusQuery critical = new StockStatusQuery(StockRisk.CRITICAL, null, null, null);

        assertTrue(critical.matches(oil));
        assertFalse(critical.matches(candles));
    }

    @Test
    void shouldMatchOnlyLowStock_whenLowStockIsTrue() {
        StockStatusQuery alert = new StockStatusQuery(null, null, true, null);

        assertTrue(alert.matches(oil));
        assertFalse(alert.matches(candles));
    }

    @Test
    void shouldSearchCategoryIgnoringCaseAndAccents() {
        assertTrue(StockStatusQuery.searching("epicerie").matches(oil));
        assertFalse(StockStatusQuery.searching("epicerie").matches(candles));
    }

    @Test
    void shouldSearchNameAndSku() {
        assertTrue(StockStatusQuery.searching("BOUG").matches(candles));
        assertTrue(StockStatusQuery.searching("sku-1").matches(oil));
    }

    @Test
    void shouldKeepOnlyLotsExpiringWithinDays_whenExpiryFilterIsGiven() {
        assertTrue(new StockStatusQuery(null, null, null, 30).matches(oil));
        assertFalse(new StockStatusQuery(null, null, null, 20).matches(oil));
        assertFalse(new StockStatusQuery(null, null, null, 30).matches(candles));
    }
}
