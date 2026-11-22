package com.hackaton.vente.payment.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.entity.SalePayment;
import com.hackaton.vente.payment.mapper.PaymentMapper;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.SaleFixtures;
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
class PaymentReaderServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SalePaymentRepository paymentRepository;

    private PaymentReaderService service;

    @BeforeEach
    void setUp() {
        service = new PaymentReaderService(saleRepository, paymentRepository, new PaymentMapper());
    }

    @Test
    void shouldReturnPaymentsInOrder_whenSaleExists() {
        Sale sale = SaleFixtures.validatedSale(7L, "2026-11-20T06:00:00Z", PaymentMethod.CREDIT,
                SaleFixtures.line(10L, "Riz", 1, "760000"));
        SalePayment first = new SalePayment(sale, new BigDecimal("300000"), PaymentMethod.MVOLA,
                Instant.parse("2026-11-21T08:00:00Z"), "MP2611", 3L);
        when(saleRepository.existsById(7L)).thenReturn(true);
        when(paymentRepository.findBySale_IdOrderByPaidAtAscIdAsc(7L)).thenReturn(List.of(first));

        List<PaymentResponse> payments = service.findBySale(7L);

        assertEquals(1, payments.size());
        assertEquals(7L, payments.get(0).saleId());
        assertEquals("MP2611", payments.get(0).reference());
    }

    @Test
    void shouldThrowNotFound_whenSaleDoesNotExist() {
        when(saleRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.findBySale(9L));
    }
}
