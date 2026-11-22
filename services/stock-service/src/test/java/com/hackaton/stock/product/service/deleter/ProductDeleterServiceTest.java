package com.hackaton.stock.product.service.deleter;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDeleterServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private PurchaseOrderRepository orderRepository;

    @InjectMocks
    private ProductDeleterService service;

    @Test
    void shouldDelete_whenProductHasNoHistory() {
        when(productRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.existsById(9L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(9L));
    }

    @Test
    void shouldRefuse_whenProductHasMovements() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(movementRepository.existsByProductId(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.delete(1L));
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void shouldRefuse_whenProductIsInAPurchaseOrder() {
        when(productRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.existsByLinesProductId(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.delete(1L));
        verify(productRepository, never()).deleteById(any());
    }
}
