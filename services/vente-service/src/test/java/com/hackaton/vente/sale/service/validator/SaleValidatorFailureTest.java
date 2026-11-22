package com.hackaton.vente.sale.service.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import com.hackaton.vente.sale.service.stock.SaleStockMover;
import com.hackaton.vente.stock.client.MovementKind;
import com.hackaton.vente.stock.client.StockClient;
import java.time.Clock;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleValidatorFailureTest {

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
    void shouldRestoreAlreadyDeductedLinesAndStayDraft_whenStockIsInsufficientOnLaterLine() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        doNothing().when(stockClient).recordMovement(eq(10L), eq(MovementKind.OUT), anyInt(), anyString());
        doThrow(new BusinessException("Stock insuffisant pour Lait"))
                .when(stockClient).recordMovement(eq(20L), eq(MovementKind.OUT), anyInt(), anyString());

        assertThrows(BusinessException.class, () -> service.validate(1L));

        verify(stockClient).recordMovement(10L, MovementKind.IN, 45, "Annulation vente #1");
        verify(stockClient, never()).recordMovement(eq(20L), eq(MovementKind.IN), anyInt(), anyString());
        verify(saleRepository, never()).save(any());
        assertEquals(DocumentStatus.DRAFT, sale.getStatus());
    }

    @Test
    void shouldStayDraftWithoutCompensation_whenStockServiceIsDown() {
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));
        doThrow(new ServiceUnavailableException("Le service Stock est indisponible"))
                .when(stockClient).recordMovement(eq(10L), eq(MovementKind.OUT), anyInt(), anyString());

        assertThrows(ServiceUnavailableException.class, () -> service.validate(1L));

        verify(stockClient, never()).recordMovement(any(), eq(MovementKind.IN), anyInt(), anyString());
        verify(saleRepository, never()).save(any());
    }

    @Test
    void shouldRejectAlreadyValidatedSaleWithoutTouchingStock() {
        sale.setStatus(DocumentStatus.VALIDATED);
        when(saleRepository.findById(1L)).thenReturn(Optional.of(sale));

        assertThrows(ImmutableResourceException.class, () -> service.validate(1L));
        verify(stockClient, never()).recordMovement(any(), any(), anyInt(), anyString());
    }

    @Test
    void shouldThrowNotFound_whenSaleDoesNotExist() {
        when(saleRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.validate(9L));
    }
}
