package com.hackaton.vente.sale.service.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.PaymentMethod;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.stock.SaleStockMover;
import com.hackaton.vente.stock.client.MovementKind;
import com.hackaton.vente.stock.client.StockClient;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleValidatorServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private StockClient stockClient;

    private SaleValidatorService service;
    private Sale sale;

    @BeforeEach
    void setUp() {
        Clock clock = SaleTestAssembler.clockAt(SaleTestAssembler.NOW);
        service = new SaleValidatorService(
                saleRepository, new SaleStockMover(stockClient), SaleTestAssembler.withoutFacts(clock), clock);
        sale = SaleFixtures.draftSale(1L,
                SaleFixtures.line(10L, "Riz", 45, "45000"),
                SaleFixtures.line(20L, "Lait", 5, "3000"));
    }

    @Test
    void shouldDeductStockForEachLineAndValidate() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> call.getArgument(0));

        SaleResponse response = service.validate(1L);

        assertEquals(DocumentStatus.VALIDATED, response.status());
        assertEquals(PaymentStatus.TO_COLLECT, response.paymentStatus());
        assertEquals(Instant.parse(SaleTestAssembler.NOW), response.validatedAt());
        verify(stockClient).recordMovement(10L, MovementKind.OUT, 45, "Vente #1");
        verify(stockClient).recordMovement(20L, MovementKind.OUT, 5, "Vente #1");
    }

    @Test
    void shouldDefaultPaymentMethodToCash_whenNoneWasChosen() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> call.getArgument(0));

        assertEquals(PaymentMethod.CASH, service.validate(1L).paymentMethod());
    }

    @Test
    void shouldKeepCreditTerms_whenSaleIsOnCredit() {
        sale.setPaymentMethod(PaymentMethod.CREDIT);
        sale.setCreditDays(30);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        when(saleRepository.save(any(Sale.class))).thenAnswer(call -> call.getArgument(0));

        SaleResponse response = service.validate(1L);

        assertEquals(PaymentMethod.CREDIT, response.paymentMethod());
        assertEquals(LocalDate.of(2026, 12, 24), response.dueDate());
    }
}
