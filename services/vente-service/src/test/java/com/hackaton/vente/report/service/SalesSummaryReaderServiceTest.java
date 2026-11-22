package com.hackaton.vente.report.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.report.dto.SalesSummaryResponse;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalesSummaryReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;

    private SalesSummaryReaderService service;

    @BeforeEach
    void setUp() {
        service = new SalesSummaryReaderService(saleRepository, SaleTestAssembler.clockAt(SaleTestAssembler.NOW));
    }

    @Test
    void shouldComputeTotalsAverageAndDailyBreakdown() {
        when(saleRepository.findEffective()).thenReturn(List.of(ReportSales.monday(), ReportSales.tuesday()));

        SalesSummaryResponse summary = service.summarize(null, null);

        assertEquals(2, summary.salesCount());
        assertEquals(new BigDecimal("17000"), summary.totalRevenue());
        assertEquals(new BigDecimal("8500.00"), summary.averageBasket());
        assertEquals(2, summary.daily().size());
        assertEquals(LocalDate.of(2026, 11, 16), summary.daily().get(0).date());
    }

    @Test
    void shouldFilterByDateRange() {
        when(saleRepository.findEffective()).thenReturn(List.of(ReportSales.monday(), ReportSales.tuesday()));

        SalesSummaryResponse summary = service.summarize(LocalDate.of(2026, 11, 17), LocalDate.of(2026, 11, 17));

        assertEquals(1, summary.salesCount());
        assertEquals(new BigDecimal("7000"), summary.totalRevenue());
    }

    @Test
    void shouldCountLateEveningSaleOnLocalDay_whenUtcDayDiffers() {
        when(saleRepository.findEffective()).thenReturn(List.of(
                ReportSales.validated(3L, "2026-11-16T22:30:00Z", SaleFixtures.line(10L, "Riz", 1, "1000"))));

        SalesSummaryResponse summary = service.summarize(LocalDate.of(2026, 11, 17), LocalDate.of(2026, 11, 17));

        assertEquals(1, summary.salesCount());
        assertEquals(LocalDate.of(2026, 11, 17), summary.daily().get(0).date());
    }

    @Test
    void shouldReturnZeros_whenThereAreNoValidatedSales() {
        when(saleRepository.findEffective()).thenReturn(List.of());

        SalesSummaryResponse summary = service.summarize(null, null);

        assertEquals(0, summary.salesCount());
        assertEquals(BigDecimal.ZERO, summary.averageBasket());
    }
}
