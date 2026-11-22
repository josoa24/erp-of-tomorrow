package com.hackaton.vente.sale.service.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class PaymentStatusResolverTest {

    private final PaymentStatusResolver resolver =
            new PaymentStatusResolver(SaleTestAssembler.clockAt("2026-11-24T07:00:00Z"));

    private Sale validated(String validatedAt, PaymentMethod method, Integer creditDays) {
        Sale sale = SaleFixtures.validatedSale(1L, validatedAt, method, SaleFixtures.line(10L, "Riz", 2, "5000"));
        sale.setCreditDays(creditDays);
        return sale;
    }

    @Test
    void shouldReturnCancelled_whenCancellationExists() {
        Sale sale = validated("2026-11-24T06:00:00Z", PaymentMethod.CASH, null);

        assertEquals(PaymentStatus.CANCELLED, resolver.resolve(sale, BigDecimal.ZERO, true));
    }

    @Test
    void shouldReturnDraft_whenSaleIsNotValidated() {
        Sale sale = SaleFixtures.draftSale(1L, SaleFixtures.line(10L, "Riz", 2, "5000"));

        assertEquals(PaymentStatus.DRAFT, resolver.resolve(sale, BigDecimal.ZERO, false));
        assertNull(resolver.dueDate(sale));
    }

    @Test
    void shouldReturnPaid_whenPaidAmountCoversTotal() {
        Sale sale = validated("2026-10-01T06:00:00Z", PaymentMethod.MVOLA, null);

        assertEquals(PaymentStatus.PAID, resolver.resolve(sale, new BigDecimal("10000"), false));
    }

    @Test
    void shouldReturnToCollect_whenCashSaleIsValidatedToday() {
        Sale sale = validated("2026-11-24T05:00:00Z", PaymentMethod.CASH, null);

        assertEquals(PaymentStatus.TO_COLLECT, resolver.resolve(sale, new BigDecimal("4000"), false));
    }

    @Test
    void shouldReturnOverdue_whenCashSaleIsUnpaidSinceYesterday() {
        Sale sale = validated("2026-11-23T10:00:00Z", PaymentMethod.CASH, null);

        assertEquals(PaymentStatus.OVERDUE, resolver.resolve(sale, BigDecimal.ZERO, false));
    }

    @Test
    void shouldReturnToCollect_whenCreditIsNotDueYet() {
        Sale sale = validated("2026-11-10T10:00:00Z", PaymentMethod.CREDIT, 30);

        assertEquals(PaymentStatus.TO_COLLECT, resolver.resolve(sale, BigDecimal.ZERO, false));
        assertEquals(LocalDate.of(2026, 12, 10), resolver.dueDate(sale));
    }

    @Test
    void shouldReturnOverdue_whenCreditDueDateHasPassed() {
        Sale sale = validated("2026-11-01T10:00:00Z", PaymentMethod.CREDIT, 15);

        assertEquals(PaymentStatus.OVERDUE, resolver.resolve(sale, BigDecimal.ZERO, false));
    }

    @Test
    void shouldUseLocalDay_whenValidatedLateInUtcEvening() {
        Sale sale = validated("2026-11-23T22:30:00Z", PaymentMethod.CASH, null);

        assertEquals(LocalDate.of(2026, 11, 24), resolver.dueDate(sale));
        assertEquals(PaymentStatus.TO_COLLECT, resolver.resolve(sale, BigDecimal.ZERO, false));
    }
}
