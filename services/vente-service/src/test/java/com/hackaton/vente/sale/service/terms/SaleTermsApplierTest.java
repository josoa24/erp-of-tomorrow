package com.hackaton.vente.sale.service.terms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.sale.dto.SaleLineRequest;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.util.List;
import org.junit.jupiter.api.Test;

class SaleTermsApplierTest {

    private final SaleTermsApplier applier = new SaleTermsApplier();
    private final Sale sale = new Sale();

    private SaleRequest request(Long customerId, PaymentMethod method, Integer creditDays, Boolean toDeliver) {
        return new SaleRequest(customerId, List.of(new SaleLineRequest(10L, 1)), method, creditDays, toDeliver);
    }

    @Test
    void shouldApplyCreditTerms_whenCreditHasCustomerAndDays() {
        applier.apply(sale, request(7L, PaymentMethod.CREDIT, 30, true));

        assertEquals(PaymentMethod.CREDIT, sale.getPaymentMethod());
        assertEquals(30, sale.getCreditDays());
        assertTrue(sale.isToDeliver());
    }

    @Test
    void shouldDropCreditDays_whenMethodIsNotCredit() {
        applier.apply(sale, request(null, PaymentMethod.MVOLA, 30, null));

        assertEquals(PaymentMethod.MVOLA, sale.getPaymentMethod());
        assertNull(sale.getCreditDays());
        assertFalse(sale.isToDeliver());
    }

    @Test
    void shouldRejectCredit_whenCreditDaysAreMissing() {
        SaleRequest withoutDays = request(7L, PaymentMethod.CREDIT, null, false);

        assertThrows(BusinessException.class, () -> applier.apply(sale, withoutDays));
    }

    @Test
    void shouldRejectCredit_whenCustomerIsMissing() {
        SaleRequest withoutCustomer = request(null, PaymentMethod.CREDIT, 15, false);

        assertThrows(BusinessException.class, () -> applier.apply(sale, withoutCustomer));
    }
}
