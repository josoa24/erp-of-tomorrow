package com.hackaton.vente.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.report.dto.SalesSummaryResponse;
import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalesReportServicesTest {

    @Mock
    private SaleRepository saleRepository;

    private Sale monday;
    private Sale tuesday;

    private Sale validated(Long id, String validatedAt, com.hackaton.vente.sale.entity.SaleLine... lines) {
        Sale sale = SaleFixtures.draftSale(id, lines);
        sale.setStatus(DocumentStatus.VALIDATED);
        sale.setValidatedAt(Instant.parse(validatedAt));
        return sale;
    }

    @BeforeEach
    void setUp() {
        monday = validated(1L, "2026-11-16T10:00:00Z", SaleFixtures.line(10L, "Riz", 10, "1000"));
        tuesday = validated(2L, "2026-11-17T10:00:00Z",
                SaleFixtures.line(10L, "Riz", 5, "1000"), SaleFixtures.line(20L, "Lait", 20, "100"));
    }

    @Test
    void shouldComputeTotalsAverageAndDailyBreakdown() {
        when(saleRepository.findByStatus(DocumentStatus.VALIDATED)).thenReturn(List.of(monday, tuesday));

        SalesSummaryResponse summary = new SalesSummaryReaderService(saleRepository).summarize(null, null);

        assertEquals(2, summary.salesCount());
        assertEquals(new BigDecimal("17000"), summary.totalRevenue());
        assertEquals(new BigDecimal("8500.00"), summary.averageBasket());
        assertEquals(2, summary.daily().size());
        assertEquals(LocalDate.of(2026, 11, 16), summary.daily().get(0).date());
    }

    @Test
    void shouldFilterByDateRange() {
        when(saleRepository.findByStatus(DocumentStatus.VALIDATED)).thenReturn(List.of(monday, tuesday));

        SalesSummaryResponse summary = new SalesSummaryReaderService(saleRepository)
                .summarize(LocalDate.of(2026, 11, 17), LocalDate.of(2026, 11, 17));

        assertEquals(1, summary.salesCount());
        assertEquals(new BigDecimal("7000"), summary.totalRevenue());
    }

    @Test
    void shouldReturnZeros_whenThereAreNoValidatedSales() {
        when(saleRepository.findByStatus(DocumentStatus.VALIDATED)).thenReturn(List.of());

        SalesSummaryResponse summary = new SalesSummaryReaderService(saleRepository).summarize(null, null);

        assertEquals(0, summary.salesCount());
        assertEquals(BigDecimal.ZERO, summary.averageBasket());
    }

    @Test
    void shouldRankProductsByQuantityAndApplyLimit() {
        when(saleRepository.findByStatus(DocumentStatus.VALIDATED)).thenReturn(List.of(monday, tuesday));

        List<TopProductResponse> top = new TopProductsReaderService(saleRepository).findTop(1);

        assertEquals(1, top.size());
        assertEquals("Lait", top.get(0).productName());
        assertEquals(20, top.get(0).totalQuantity());
    }

    @Test
    void shouldAggregateSameProductAcrossSales() {
        when(saleRepository.findByStatus(DocumentStatus.VALIDATED)).thenReturn(List.of(monday, tuesday));

        List<TopProductResponse> top = new TopProductsReaderService(saleRepository).findTop(5);

        TopProductResponse rice = top.stream().filter(p -> p.productId() == 10L).findFirst().orElseThrow();
        assertEquals(15, rice.totalQuantity());
        assertEquals(new BigDecimal("15000"), rice.totalRevenue());
    }
}
