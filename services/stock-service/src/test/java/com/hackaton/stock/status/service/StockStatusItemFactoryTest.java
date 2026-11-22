package com.hackaton.stock.status.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.lot.config.ExpiryProperties;
import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.service.expiry.ExpiryPolicy;
import com.hackaton.stock.product.ProductFixtures;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusLabel;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class StockStatusItemFactoryTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-11-22T06:00:00Z"), ZoneId.of("Indian/Antananarivo"));

    private final StockStatusItemFactory factory = new StockStatusItemFactory(
            new AutonomyCalculator(), new ExpiryPolicy(CLOCK, new ExpiryProperties(30)));

    private Product product(int quantity, int threshold) {
        Product product = ProductFixtures.product(1L, "Riz makalioka 50 kg", quantity, threshold);
        product.setCategory("Épicerie");
        product.setPurchasePrice(new BigDecimal("142000"));
        return product;
    }

    @Test
    void shouldFlagEmptyStockAsRuptureRiskEvenWithoutSales() {
        StockStatusItem item = factory.create(product(0, 5), 0, null);

        assertEquals(StockRisk.CRITICAL, item.risk());
        assertEquals(StockStatusLabel.RUPTURE_RISK, item.statusLabel());
        assertNull(item.daysRemaining());
    }

    @Test
    void shouldFlagLowStockAsBelowThreshold_whenNothingWasSold() {
        StockStatusItem item = factory.create(product(3, 5), 0, null);

        assertEquals(StockRisk.WARNING, item.risk());
        assertEquals(StockStatusLabel.BELOW_THRESHOLD, item.statusLabel());
        assertTrue(item.lowStock());
    }

    @Test
    void shouldComputeAverageAndDaysFromSevenDayWindow() {
        StockStatusItem item = factory.create(product(19, 5), 70, null);

        assertEquals(new BigDecimal("10.00"), item.averageDailyOut());
        assertEquals(new BigDecimal("1.9"), item.daysRemaining());
        assertEquals(StockStatusLabel.RUPTURE_RISK, item.statusLabel());
    }

    @Test
    void shouldExposeCategoryAndPurchasePrice() {
        StockStatusItem item = factory.create(product(40, 10), 0, null);

        assertEquals("Épicerie", item.category());
        assertEquals(new BigDecimal("142000"), item.purchasePrice());
        assertEquals(StockStatusLabel.IN_STOCK, item.statusLabel());
    }

    @Test
    void shouldExposeEarliestOpenLot_whenProductHasOne() {
        LotBalance lot = new LotBalance(4L, 1L, "LC-2611", LocalDate.of(2026, 12, 15), 48, 30,
                Instant.parse("2026-11-02T07:00:00Z"));

        StockStatusItem item = factory.create(product(40, 10), 0, lot);

        assertEquals("LC-2611", item.lotNumber());
        assertEquals(LocalDate.of(2026, 12, 15), item.expiryDate());
        assertEquals(23, item.daysToExpiry());
        assertTrue(item.expiringSoon());
    }

    @Test
    void shouldLeaveLotFieldsEmpty_whenProductHasNoOpenLot() {
        StockStatusItem item = factory.create(product(40, 10), 0, null);

        assertNull(item.lotNumber());
        assertNull(item.expiryDate());
        assertNull(item.daysToExpiry());
        assertFalse(item.expiringSoon());
    }
}
