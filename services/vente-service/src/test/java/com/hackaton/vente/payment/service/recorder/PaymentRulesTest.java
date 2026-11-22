package com.hackaton.vente.payment.service.recorder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PaymentRulesTest {

    private final PaymentRules rules = new PaymentRules();
    private final Sale sale = SaleFixtures.validatedSale(411L, "2026-11-24T06:00:00Z", PaymentMethod.CREDIT,
            SaleFixtures.line(10L, "Riz", 1, "420000"));

    @Test
    void shouldAccept_whenAmountEqualsRemaining() {
        assertDoesNotThrow(() -> rules.check(
                sale, new BigDecimal("220000"), PaymentMethod.MVOLA, new BigDecimal("200000"), false));
    }

    @Test
    void shouldReject_whenAmountExceedsRemaining() {
        BusinessException error = assertThrows(BusinessException.class, () -> rules.check(
                sale, new BigDecimal("220001"), PaymentMethod.CASH, new BigDecimal("200000"), false));

        assertEquals("Le montant dépasse le reste à encaisser de la vente V-0411 : 220000 Ar", error.getMessage());
    }

    @Test
    void shouldReject_whenSaleIsDraft() {
        Sale draft = SaleFixtures.draftSale(5L, SaleFixtures.line(10L, "Riz", 1, "1000"));

        assertThrows(BusinessException.class,
                () -> rules.check(draft, BigDecimal.ONE, PaymentMethod.CASH, BigDecimal.ZERO, false));
    }

    @Test
    void shouldReject_whenSaleIsCancelled() {
        assertThrows(BusinessException.class,
                () -> rules.check(sale, BigDecimal.ONE, PaymentMethod.CASH, BigDecimal.ZERO, true));
    }

    @Test
    void shouldReject_whenMethodIsCredit() {
        assertThrows(BusinessException.class,
                () -> rules.check(sale, BigDecimal.ONE, PaymentMethod.CREDIT, BigDecimal.ZERO, false));
    }
}
