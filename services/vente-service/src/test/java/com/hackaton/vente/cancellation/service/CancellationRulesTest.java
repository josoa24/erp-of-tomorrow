package com.hackaton.vente.cancellation.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CancellationRulesTest {

    @Mock
    private SaleCancellationRepository cancellationRepository;
    @Mock
    private SalePaymentRepository paymentRepository;
    @InjectMocks
    private CancellationRules rules;

    private final Sale sale = SaleFixtures.validatedSale(7L, "2026-11-24T06:00:00Z", PaymentMethod.CASH,
            SaleFixtures.line(10L, "Riz", 1, "1000"));

    @Test
    void shouldAccept_whenSaleIsValidatedUnpaidAndNotCancelled() {
        when(cancellationRepository.existsBySale_Id(7L)).thenReturn(false);
        when(paymentRepository.existsBySale_Id(7L)).thenReturn(false);

        assertDoesNotThrow(() -> rules.check(sale));
    }

    @Test
    void shouldReject_whenSaleIsDraft() {
        Sale draft = SaleFixtures.draftSale(5L, SaleFixtures.line(10L, "Riz", 1, "1000"));

        assertThrows(BusinessException.class, () -> rules.check(draft));
    }

    @Test
    void shouldReject_whenSaleIsAlreadyCancelled() {
        when(cancellationRepository.existsBySale_Id(7L)).thenReturn(true);

        BusinessException error = assertThrows(BusinessException.class, () -> rules.check(sale));

        assertEquals("La vente V-0007 est déjà annulée", error.getMessage());
    }

    @Test
    void shouldReject_whenSaleHasPayment() {
        when(cancellationRepository.existsBySale_Id(7L)).thenReturn(false);
        when(paymentRepository.existsBySale_Id(7L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> rules.check(sale));
    }
}
