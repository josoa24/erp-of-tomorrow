package com.hackaton.stock.status.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.hackaton.stock.movement.dto.ProductOutQuantity;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockStatusReaderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository movementRepository;

    private StockStatusReaderService service;

    private Product product(Long id, String name, int quantity, int threshold) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setSku("SKU-" + id);
        product.setUnit("sac");
        product.setQuantity(quantity);
        product.setMinThreshold(threshold);
        product.setUnitPrice(BigDecimal.TEN);
        return product;
    }

    @BeforeEach
    void setUp() {
        service = new StockStatusReaderService(productRepository, movementRepository, new AutonomyCalculator());
    }

    private void givenSales(ProductOutQuantity... sales) {
        when(movementRepository.sumQuantityByProductSince(eq(MovementType.OUT), any(Instant.class)))
                .thenReturn(List.of(sales));
    }

    @Test
    void shouldSortMostUrgentFirst() {
        when(productRepository.findAll()).thenReturn(List.of(
                product(1L, "Sucre", 500, 10),
                product(2L, "Riz", 19, 5),
                product(3L, "Lait", 40, 5)));
        givenSales(new ProductOutQuantity(1L, 70L), new ProductOutQuantity(2L, 70L), new ProductOutQuantity(3L, 70L));

        List<StockStatusItem> status = service.findAll(null);

        assertEquals(List.of("Riz", "Lait", "Sucre"), status.stream().map(StockStatusItem::productName).toList());
        assertEquals(List.of(StockRisk.CRITICAL, StockRisk.WARNING, StockRisk.OK),
                status.stream().map(StockStatusItem::risk).toList());
    }

    @Test
    void shouldFlagEmptyStockAsCriticalEvenWithoutSales() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Riz", 0, 5)));
        givenSales();

        StockStatusItem item = service.findAll(null).get(0);

        assertEquals(StockRisk.CRITICAL, item.risk());
        assertNull(item.daysRemaining());
    }

    @Test
    void shouldFlagLowStockAsWarningWhenNothingWasSold() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Riz", 3, 5), product(2L, "Lait", 50, 5)));
        givenSales();

        List<StockStatusItem> status = service.findAll(null);

        assertEquals(StockRisk.WARNING, status.get(0).risk());
        assertTrue(status.get(0).lowStock());
        assertEquals(StockRisk.OK, status.get(1).risk());
    }

    @Test
    void shouldKeepOnlyRequestedRisk() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Riz", 19, 5), product(2L, "Lait", 500, 5)));
        givenSales(new ProductOutQuantity(1L, 70L));

        List<StockStatusItem> critical = service.findAll(StockRisk.CRITICAL);

        assertEquals(1, critical.size());
        assertEquals("Riz", critical.get(0).productName());
    }

    @Test
    void shouldReturnEmptyList_whenThereAreNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());
        givenSales();

        assertTrue(service.findAll(null).isEmpty());
    }

    @Test
    void shouldComputeAverageAndDaysFromSevenDayWindow() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Riz", 19, 5)));
        givenSales(new ProductOutQuantity(1L, 70L));

        StockStatusItem item = service.findAll(null).get(0);

        assertEquals(new BigDecimal("10.00"), item.averageDailyOut());
        assertEquals(new BigDecimal("1.9"), item.daysRemaining());
    }
}
