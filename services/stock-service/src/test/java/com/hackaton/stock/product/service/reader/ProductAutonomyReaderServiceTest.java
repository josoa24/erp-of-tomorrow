package com.hackaton.stock.product.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductAutonomyReaderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository movementRepository;

    private ProductAutonomyReaderService service;

    @BeforeEach
    void setUp() {
        service = new ProductAutonomyReaderService(productRepository, movementRepository, new AutonomyCalculator());
    }

    @Test
    void shouldComputeRisk_fromOutMovementsOfTheLastSevenDays() {
        Product rice = new Product();
        rice.setId(1L);
        rice.setName("Riz blanc");
        rice.setQuantity(19);
        when(productRepository.findById(1L)).thenReturn(Optional.of(rice));
        when(movementRepository.sumQuantitySince(eq(1L), eq(MovementType.OUT), any(Instant.class))).thenReturn(70L);

        AutonomyResponse response = service.analyze(1L);

        assertEquals(StockRisk.CRITICAL, response.risk());
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.analyze(2L));
    }
}
