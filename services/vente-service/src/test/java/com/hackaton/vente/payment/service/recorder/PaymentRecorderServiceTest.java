package com.hackaton.vente.payment.service.recorder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.payment.dto.PaymentRequest;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.entity.SalePayment;
import com.hackaton.vente.payment.mapper.PaymentMapper;
import com.hackaton.vente.payment.repository.SalePaymentRepository;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentRecorderServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SalePaymentRepository paymentRepository;
    @Mock
    private SaleCancellationRepository cancellationRepository;

    private PaymentRecorderService service;
    private final Sale sale = SaleFixtures.validatedSale(411L, "2026-11-24T06:00:00Z", PaymentMethod.CASH,
            SaleFixtures.line(10L, "Riz", 1, "420000"));

    @BeforeEach
    void setUp() {
        service = new PaymentRecorderService(saleRepository, paymentRepository, cancellationRepository,
                new PaymentRules(), new PaymentMapper(), SaleTestAssembler.clockAt(SaleTestAssembler.NOW));
    }

    @Test
    void shouldRecordPaymentWithAuthorAndTime_whenAmountFitsRemaining() {
        when(saleRepository.findWithLockById(411L)).thenReturn(Optional.of(sale));
        when(paymentRepository.sumAmountBySaleId(411L)).thenReturn(new BigDecimal("100000"));
        when(cancellationRepository.existsBySale_Id(411L)).thenReturn(false);
        when(paymentRepository.save(any(SalePayment.class))).thenAnswer(call -> call.getArgument(0));

        PaymentResponse response = service.record(
                411L, new PaymentRequest(new BigDecimal("320000"), PaymentMethod.ORANGE_MONEY, "  "), 3L);

        assertEquals(411L, response.saleId());
        assertEquals(new BigDecimal("320000"), response.amount());
        assertEquals(PaymentMethod.ORANGE_MONEY, response.method());
        assertEquals(Instant.parse(SaleTestAssembler.NOW), response.paidAt());
        assertEquals(3L, response.recordedBy());
        assertNull(response.reference());
    }

    @Test
    void shouldNotSave_whenAmountExceedsRemaining() {
        when(saleRepository.findWithLockById(411L)).thenReturn(Optional.of(sale));
        when(paymentRepository.sumAmountBySaleId(411L)).thenReturn(new BigDecimal("420000"));
        when(cancellationRepository.existsBySale_Id(411L)).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.record(
                411L, new PaymentRequest(BigDecimal.ONE, PaymentMethod.CASH, null), 3L));
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFound_whenSaleDoesNotExist() {
        when(saleRepository.findWithLockById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.record(
                9L, new PaymentRequest(BigDecimal.ONE, PaymentMethod.CASH, null), 3L));
    }
}
