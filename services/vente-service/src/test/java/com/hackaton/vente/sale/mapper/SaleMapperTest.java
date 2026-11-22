package com.hackaton.vente.sale.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.service.facts.SaleFacts;
import com.hackaton.vente.sale.service.status.PaymentStatusResolver;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SaleMapperTest {

    private final SaleMapper mapper =
            new SaleMapper(new PaymentStatusResolver(SaleTestAssembler.clockAt(SaleTestAssembler.NOW)));

    @Test
    void shouldExposeNumberCustomerNameAndPaidAmount_whenFactsAreKnown() {
        Sale sale = SaleFixtures.validatedSale(412L, "2026-11-24T06:00:00Z", PaymentMethod.MVOLA,
                SaleFixtures.line(10L, "Riz", 2, "93000"));
        sale.setCustomerId(7L);
        sale.setToDeliver(true);
        SaleFacts facts = new SaleFacts(Map.of(7L, "Épicerie Rova"), Map.of(412L, new BigDecimal("186000")), Set.of());

        SaleResponse response = mapper.toResponse(sale, facts);

        assertEquals("V-0412", response.number());
        assertEquals("Épicerie Rova", response.customerName());
        assertEquals(new BigDecimal("186000"), response.paidAmount());
        assertEquals(PaymentStatus.PAID, response.paymentStatus());
        assertEquals(PaymentMethod.MVOLA, response.paymentMethod());
        assertTrue(response.toDeliver());
    }

    @Test
    void shouldNameWalkInCustomerAndDefaultPaidToZero_whenNoCustomerNorPayment() {
        Sale sale = SaleFixtures.draftSale(9L, SaleFixtures.line(10L, "Riz", 1, "32500"));

        SaleResponse response = mapper.toResponse(sale, SaleFacts.none());

        assertEquals("V-0009", response.number());
        assertEquals("Client comptoir", response.customerName());
        assertEquals(BigDecimal.ZERO, response.paidAmount());
        assertEquals(PaymentStatus.DRAFT, response.paymentStatus());
    }

    @Test
    void shouldReportCancelled_whenSaleIsInCancelledSet() {
        Sale sale = SaleFixtures.validatedSale(3L, "2026-11-20T06:00:00Z", PaymentMethod.CASH,
                SaleFixtures.line(10L, "Riz", 1, "1000"));

        SaleResponse response = mapper.toResponse(sale, new SaleFacts(Map.of(), Map.of(), Set.of(3L)));

        assertEquals(PaymentStatus.CANCELLED, response.paymentStatus());
    }
}
