package com.hackaton.stock.summary.service.reader;

import static com.hackaton.stock.status.StatusItemFixtures.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.status.dto.StockStatusQuery;
import com.hackaton.stock.status.service.StockStatusReader;
import com.hackaton.stock.summary.dto.StockSummaryResponse;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockSummaryReaderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockStatusReader statusReader;
    @InjectMocks
    private StockSummaryReaderService service;

    private void givenCatalog(long total, long lowStock) {
        when(productRepository.count()).thenReturn(total);
        when(productRepository.countLowStock()).thenReturn(lowStock);
    }

    @Test
    void shouldAggregateCountsAndStockValue() {
        givenCatalog(12L, 3L);
        when(productRepository.totalStockValue()).thenReturn(new BigDecimal("855000.00"));

        StockSummaryResponse summary = service.summarize();

        assertEquals(12L, summary.totalProducts());
        assertEquals(3L, summary.lowStockProducts());
        assertEquals(new BigDecimal("855000.00"), summary.totalStockValue());
    }

    @Test
    void shouldComputeAvailabilityAsShareOfProductsAboveThreshold() {
        givenCatalog(64L, 5L);

        assertEquals(new BigDecimal("0.9219"), service.summarize().availabilityRate());
    }

    @Test
    void shouldReturnZeroAvailability_whenThereAreNoProducts() {
        givenCatalog(0L, 0L);

        assertEquals(BigDecimal.ZERO, service.summarize().availabilityRate());
    }

    @Test
    void shouldValueStockAtPurchasePriceAndCountMissingPrices() {
        givenCatalog(5L, 1L);
        when(productRepository.totalPurchaseValue()).thenReturn(new BigDecimal("8420000.00"));
        when(productRepository.countByPurchasePriceIsNull()).thenReturn(2L);

        StockSummaryResponse summary = service.summarize();

        assertEquals(new BigDecimal("8420000.00"), summary.totalPurchaseValue());
        assertEquals(2L, summary.productsWithoutPurchasePrice());
    }

    @Test
    void shouldCountCriticalLowStockAndExpiringSoon() {
        givenCatalog(4L, 2L);
        when(statusReader.findAll(StockStatusQuery.everything())).thenReturn(List.of(
                item(1L, "Huile 5 L", "Épicerie", StockRisk.CRITICAL, true, 23),
                item(2L, "Savon", null, StockRisk.CRITICAL, false, null),
                item(3L, "Sucre", null, StockRisk.WARNING, true, 40),
                item(4L, "Lait", null, StockRisk.OK, false, 12)));

        StockSummaryResponse summary = service.summarize();

        assertEquals(1L, summary.criticalLowStock());
        assertEquals(2L, summary.expiringSoon());
    }
}
