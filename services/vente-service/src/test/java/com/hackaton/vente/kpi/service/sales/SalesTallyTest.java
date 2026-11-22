package com.hackaton.vente.kpi.service.sales;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

class SalesTallyTest {

    @Test
    void shouldCountOnlySalesOfTheLocalPeriod() {
        SalesTally tally = SalesTally.of(List.of(
                SaleFixtures.validatedSale(1L, "2026-02-28T20:59:00Z", PaymentMethod.CASH,
                        SaleFixtures.line(10L, "Riz", 1, "1000")),
                SaleFixtures.validatedSale(2L, "2026-02-28T21:00:00Z", PaymentMethod.CASH,
                        SaleFixtures.line(10L, "Riz", 1, "2000"))),
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31), SaleTestAssembler.ZONE);

        assertEquals(1, tally.count());
        assertEquals(new BigDecimal("2000"), tally.revenue());
        assertEquals(new BigDecimal("2000.00"), tally.averageBasket());
    }
}
