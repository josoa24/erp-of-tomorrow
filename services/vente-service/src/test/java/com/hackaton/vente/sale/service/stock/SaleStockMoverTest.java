package com.hackaton.vente.sale.service.stock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.sale.SaleFixtures;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.stock.client.MovementKind;
import com.hackaton.vente.stock.client.StockClient;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SaleStockMoverTest {

    private static final StockMove REENTRY =
            new StockMove(MovementKind.IN, "Annulation V-0007", "Échec annulation V-0007");

    @Mock
    private StockClient stockClient;
    @InjectMocks
    private SaleStockMover mover;

    private final List<SaleLine> lines = List.of(
            SaleFixtures.line(10L, "Riz", 3, "1000"), SaleFixtures.line(20L, "Huile", 2, "9000"));

    @Test
    void shouldMoveEveryLineThenComplete_whenStockAccepts() {
        String result = mover.moveThen(lines, REENTRY, () -> "ok");

        assertEquals("ok", result);
        verify(stockClient).recordMovement(10L, MovementKind.IN, 3, "Annulation V-0007");
        verify(stockClient).recordMovement(20L, MovementKind.IN, 2, "Annulation V-0007");
    }

    @Test
    void shouldCompensateMovedLines_whenLaterLineFails() {
        lenient().doThrow(new BusinessException("Produit introuvable"))
                .when(stockClient).recordMovement(eq(20L), eq(MovementKind.IN), anyInt(), anyString());

        assertThrows(BusinessException.class, () -> mover.moveThen(lines, REENTRY, () -> "ok"));

        verify(stockClient).recordMovement(10L, MovementKind.OUT, 3, "Échec annulation V-0007");
    }

    @Test
    void shouldCompensateAllLines_whenCompletionFails() {
        assertThrows(IllegalStateException.class, () -> mover.moveThen(lines, REENTRY, () -> {
            throw new IllegalStateException("base indisponible");
        }));

        verify(stockClient).recordMovement(10L, MovementKind.OUT, 3, "Échec annulation V-0007");
        verify(stockClient).recordMovement(20L, MovementKind.OUT, 2, "Échec annulation V-0007");
    }

    @Test
    void shouldKeepOriginalError_whenCompensationAlsoFails() {
        lenient().doThrow(new BusinessException("Produit introuvable"))
                .when(stockClient).recordMovement(eq(20L), eq(MovementKind.IN), anyInt(), anyString());
        lenient().doThrow(new BusinessException("Stock insuffisant"))
                .when(stockClient).recordMovement(eq(10L), eq(MovementKind.OUT), anyInt(), anyString());

        BusinessException error =
                assertThrows(BusinessException.class, () -> mover.moveThen(lines, REENTRY, () -> "ok"));

        assertEquals("Produit introuvable", error.getMessage());
    }
}
