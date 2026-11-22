package com.hackaton.vente.cancellation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.vente.cancellation.dto.CancellationRequest;
import com.hackaton.vente.cancellation.entity.SaleCancellation;
import com.hackaton.vente.cancellation.repository.SaleCancellationRepository;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.SaleTestAssembler;
import com.hackaton.vente.sale.entity.PaymentMethod;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleCancellerServiceTest {

    @Mock
    private SaleRepository saleRepository;
    @Mock
    private SaleCancellationRepository cancellationRepository;
    @Mock
    private CancellationRules rules;
    @Mock
    private StockClient stockClient;

    private SaleCancellerService service;
    private final Sale sale = SaleFixtures.validatedSale(7L, "2026-11-24T06:00:00Z", PaymentMethod.CASH,
            SaleFixtures.line(10L, "Riz", 3, "1000"), SaleFixtures.line(20L, "Huile", 2, "9000"));

    @BeforeEach
    void setUp() {
        Clock clock = SaleTestAssembler.clockAt(SaleTestAssembler.NOW);
        service = new SaleCancellerService(saleRepository, cancellationRepository, rules,
                new SaleStockMover(stockClient), SaleTestAssembler.withoutFacts(clock), clock);
    }

    @Test
    void shouldReenterStockAndRecordCancellation_whenRulesAccept() {
        when(saleRepository.findWithLockById(7L)).thenReturn(Optional.of(sale));

        service.cancel(7L, new CancellationRequest("  Erreur de saisie  "), 3L);

        verify(stockClient).recordMovement(10L, MovementKind.IN, 3, "Annulation V-0007");
        verify(stockClient).recordMovement(20L, MovementKind.IN, 2, "Annulation V-0007");
        ArgumentCaptor<SaleCancellation> saved = ArgumentCaptor.forClass(SaleCancellation.class);
        verify(cancellationRepository).saveAndFlush(saved.capture());
        assertEquals("Erreur de saisie", saved.getValue().getReason());
        assertEquals(3L, saved.getValue().getCancelledBy());
    }

    @Test
    void shouldRestoreStockAndNotRecord_whenLaterLineFails() {
        when(saleRepository.findWithLockById(7L)).thenReturn(Optional.of(sale));
        lenient().doThrow(new ServiceUnavailableException("Le service Stock est indisponible"))
                .when(stockClient).recordMovement(eq(20L), eq(MovementKind.IN), anyInt(), anyString());

        assertThrows(ServiceUnavailableException.class,
                () -> service.cancel(7L, new CancellationRequest("Retour client"), 3L));

        verify(stockClient).recordMovement(10L, MovementKind.OUT, 3, "Échec annulation V-0007");
        verify(cancellationRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldThrowNotFound_whenSaleDoesNotExist() {
        when(saleRepository.findWithLockById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.cancel(9L, new CancellationRequest("Retour client"), 3L));
    }
}
