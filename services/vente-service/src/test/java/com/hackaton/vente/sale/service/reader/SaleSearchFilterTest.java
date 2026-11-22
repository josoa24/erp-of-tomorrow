package com.hackaton.vente.sale.service.reader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.controller.SaleResponseFixtures;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.dto.SaleSearchCriteria;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaleSearchFilterTest {

    private final SaleSearchFilter filter = new SaleSearchFilter(SaleTestAssembler.clockAt(SaleTestAssembler.NOW));

    private SaleSearchCriteria criteria(Long customerId, PaymentMethod method, Boolean toDeliver, LocalDate day) {
        return new SaleSearchCriteria(customerId, DocumentStatus.VALIDATED, day, day, null, null, method, toDeliver);
    }

    private SaleSearchCriteria derived(String search, List<PaymentStatus> statuses) {
        return new SaleSearchCriteria(null, null, null, null, search, statuses, null, null);
    }

    private Sale creditSale() {
        Sale sale = SaleFixtures.validatedSale(411L, "2026-11-23T22:30:00Z", PaymentMethod.CREDIT,
                SaleFixtures.line(10L, "Riz", 1, "420000"));
        sale.setCustomerId(7L);
        sale.setToDeliver(true);
        return sale;
    }

    @Test
    void shouldMatchStoredFields_whenAllCriteriaAgree() {
        SaleSearchCriteria matching = criteria(7L, PaymentMethod.CREDIT, true, LocalDate.of(2026, 11, 24));

        assertTrue(filter.matchesStored(creditSale(), matching));
    }

    @Test
    void shouldRejectStoredSale_whenLocalDayIsOutsideRange() {
        assertFalse(filter.matchesStored(creditSale(), criteria(7L, null, null, LocalDate.of(2026, 11, 23))));
    }

    @Test
    void shouldRejectStoredSale_whenMethodOrDeliveryDiffer() {
        assertFalse(filter.matchesStored(creditSale(), criteria(null, PaymentMethod.MVOLA, null, null)));
        assertFalse(filter.matchesStored(creditSale(), criteria(null, null, false, null)));
        assertFalse(filter.matchesStored(creditSale(), criteria(8L, null, null, null)));
    }

    @Test
    void shouldMatchSearchOnNumberOrAccentlessCustomerName() {
        SaleResponse sale = SaleResponseFixtures.sale(412L, DocumentStatus.VALIDATED);

        assertTrue(filter.matchesDerived(sale, derived("0412", null)));
        assertTrue(filter.matchesDerived(sale, derived("epicerie", null)));
        assertFalse(filter.matchesDerived(sale, derived("hotel", null)));
    }

    @Test
    void shouldFilterOnAnyOfThePaymentStatuses() {
        SaleResponse overdue = SaleResponseFixtures.sale(408L, DocumentStatus.VALIDATED, PaymentStatus.OVERDUE);

        List<PaymentStatus> toCollect = List.of(PaymentStatus.TO_COLLECT, PaymentStatus.OVERDUE);

        assertTrue(filter.matchesDerived(overdue, derived(null, toCollect)));
        assertFalse(filter.matchesDerived(overdue, derived("", List.of(PaymentStatus.PAID))));
    }
}
