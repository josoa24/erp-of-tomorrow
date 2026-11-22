package com.hackaton.stock.movement.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.InsufficientStockException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.lot.service.tracker.MovementLotTracker;
import com.hackaton.stock.movement.dto.MovementRequest;
import com.hackaton.stock.movement.dto.MovementResponse;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.movement.mapper.StockMovementMapper;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockMovementCreatorServiceTest {

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
        product.setName("Riz");
        product.setQuantity(19);
    }

    @Test
    void shouldIncreaseStock_whenMovementIsIn() {
        when(productRepository.findForUpdateById(1L)).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(call -> call.getArgument(0));

        MovementResponse response = service.record(1L, new MovementRequest(MovementType.IN, 40, "livraison"));

        assertEquals(59, response.stockAfter());
        assertEquals(59, product.getQuantity());
    }

    @Test
    void shouldDecreaseStock_whenMovementIsOut() {
        when(productRepository.findForUpdateById(1L)).thenReturn(Optional.of(product));
        when(movementRepository.save(any(StockMovement.class))).thenAnswer(call -> call.getArgument(0));

        MovementResponse response = service.record(1L, new MovementRequest(MovementType.OUT, 9, "vente"));

        assertEquals(10, response.stockAfter());
    }

    @Test
    void shouldThrowInsufficientStock_whenOutExceedsQuantity() {
        when(productRepository.findForUpdateById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class,
                () -> service.record(1L, new MovementRequest(MovementType.OUT, 45, "vente")));
        verify(movementRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findForUpdateById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.record(2L, new MovementRequest(MovementType.IN, 1, null)));
    }
}
