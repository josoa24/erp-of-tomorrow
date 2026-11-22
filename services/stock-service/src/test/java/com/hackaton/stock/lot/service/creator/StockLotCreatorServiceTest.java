package com.hackaton.stock.lot.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.lot.entity.StockLot;
import com.hackaton.stock.lot.repository.StockLotRepository;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockLotCreatorServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-22T06:00:00Z");
    private static final LocalDate EXPIRY = LocalDate.of(2026, 12, 15);

    @Mock
    private StockLotRepository lotRepository;

    private StockLotCreatorService service;
    private Product product;
    private StockMovement movement;

    @BeforeEach
    void setUp() {
        service = new StockLotCreatorService(lotRepository, Clock.fixed(NOW, ZoneId.of("Indian/Antananarivo")));
        product = new Product();
        product.setId(3L);
        product.setName("Lait concentré");
        movement = new StockMovement();
        movement.setProductId(3L);
        movement.setType(MovementType.IN);
        movement.setQuantity(48);
    }

    @Test
    void shouldSaveLotWithMovementQuantity_whenLotNumberIsNew() {
        when(lotRepository.existsByProductIdAndLotNumber(3L, "LC-2611")).thenReturn(false);

        service.receive(product, movement, "LC-2611", EXPIRY);

        ArgumentCaptor<StockLot> captor = ArgumentCaptor.forClass(StockLot.class);
        verify(lotRepository).save(captor.capture());
        StockLot lot = captor.getValue();
        assertSame(product, lot.getProduct());
        assertSame(movement, lot.getMovement());
        assertEquals("LC-2611", lot.getLotNumber());
        assertEquals(EXPIRY, lot.getExpiryDate());
        assertEquals(48, lot.getQuantity());
        assertEquals(NOW, lot.getReceivedAt());
    }

    @Test
    void shouldThrowDuplicate_whenLotNumberAlreadyExistsForProduct() {
        when(lotRepository.existsByProductIdAndLotNumber(3L, "LC-2611")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.receive(product, movement, "LC-2611", EXPIRY));
        verify(lotRepository, never()).save(any());
    }
}
