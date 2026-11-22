package com.hackaton.stock.movement.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.lot.service.tracker.MovementLotTracker;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.movement.mapper.StockMovementMapper;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementCreatorLotTest {

    private static final MovementRequest ENTRY_WITH_LOT =
            new MovementRequest(MovementType.IN, 48, "Livraison", "LC-2611", LocalDate.of(2026, 12, 15));

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private MovementLotTracker lotTracker;

    private StockMovementCreatorService service;
    private Product product;

    @BeforeEach
    void setUp() {
        service = new StockMovementCreatorService(
                productRepository, movementRepository, new StockMovementMapper(), lotTracker);
        product = new Product();
        product.setId(3L);
        product.setName("Lait concentré");
        product.setQuantity(4);
    }

    @Test
    void shouldTrackLotOfSavedMovement_whenEntryIsRecorded() {
        when(productRepository.findById(3L)).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(call -> call.getArgument(0));

        service.record(3L, ENTRY_WITH_LOT);

        InOrder order = inOrder(lotTracker, movementRepository);
        order.verify(lotTracker).check(ENTRY_WITH_LOT);
        order.verify(movementRepository).save(any(StockMovement.class));
        order.verify(lotTracker).track(any(Product.class), any(StockMovement.class), any(MovementRequest.class));
        assertEquals(52, product.getQuantity());
    }

    @Test
    void shouldNotTouchStock_whenLotRulesAreBroken() {
        doThrow(new InvalidRequestException("lot")).when(lotTracker).check(ENTRY_WITH_LOT);

        assertThrows(InvalidRequestException.class, () -> service.record(3L, ENTRY_WITH_LOT));
        verify(productRepository, never()).save(any());
        verify(movementRepository, never()).save(any());
    }
}
