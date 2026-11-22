package com.hackaton.vente.kpi.service.sales;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.hackaton.vente.kpi.dto.SalesKpiResponse;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SalesKpiReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SalePaymentRepository paymentRepository;

    private SalesKpiReaderService service;

    @BeforeEach
    void setUp() {
        service = new SalesKpiReaderService(
                saleRepository, paymentRepository, SaleTestAssembler.clockAt("2026-11-24T07:00:00Z"));
    }

    private Sale sale(Long id, String validatedAt, String amount) {
        return SaleFixtures.validatedSale(
                id, validatedAt, PaymentMethod.CASH, SaleFixtures.line(10L, "Riz", 1, amount));
    }

    @Test
    void shouldComputeMonthTodayAndComparisons_inLocalTimeZone() {
        when(saleRepository.findEffectiveValidatedSince(Instant.parse("2026-09-30T21:00:00Z"))).thenReturn(List.of(
                sale(1L, "2026-10-05T08:00:00Z", "100000"),
                sale(2L, "2026-10-24T20:00:00Z", "50000"),
                sale(3L, "2026-10-25T08:00:00Z", "999000"),
                sale(4L, "2026-11-17T08:00:00Z", "40000"),
                sale(5L, "2026-11-23T22:00:00Z", "186000"),
                sale(6L, "2026-11-24T06:00:00Z", "32500")));
        when(paymentRepository.sumAmountPaidBetween(
                Instant.parse("2026-11-23T21:00:00Z"), Instant.parse("2026-11-24T21:00:00Z")))
                .thenReturn(new BigDecimal("150000"));

        SalesKpiResponse kpis = service.read();

        assertEquals(new BigDecimal("258500"), kpis.monthRevenue());
        assertEquals(new BigDecimal("150000"), kpis.previousMonthRevenue());
        assertEquals(2, kpis.todaySalesCount());
        assertEquals(new BigDecimal("218500"), kpis.todayRevenue());
        assertEquals(new BigDecimal("150000"), kpis.todayCollected());
        assertEquals(new BigDecimal("40000"), kpis.sameWeekdayLastWeekRevenue());
        assertEquals(new BigDecimal("86166.67"), kpis.averageBasket());
        assertEquals(new BigDecimal("75000.00"), kpis.previousMonthAverageBasket());
    }

    @Test
    void shouldReturnZeroAverages_whenThereAreNoSales() {
        when(saleRepository.findEffectiveValidatedSince(Instant.parse("2026-09-30T21:00:00Z"))).thenReturn(List.of());
        when(paymentRepository.sumAmountPaidBetween(
                Instant.parse("2026-11-23T21:00:00Z"), Instant.parse("2026-11-24T21:00:00Z")))
                .thenReturn(BigDecimal.ZERO);

        SalesKpiResponse kpis = service.read();

        assertEquals(BigDecimal.ZERO, kpis.monthRevenue());
        assertEquals(0, kpis.todaySalesCount());
        assertEquals(BigDecimal.ZERO, kpis.averageBasket());
    }
}
