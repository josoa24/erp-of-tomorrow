package com.hackaton.stock.replenishment.service;

import static com.hackaton.stock.replenishment.service.ReplenishmentFixtures.oneUnitOutPerDay;
import static com.hackaton.stock.replenishment.service.ReplenishmentFixtures.product;
import static com.hackaton.stock.replenishment.service.ReplenishmentFixtures.unitOut;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.replenishment.config.ReplenishmentProperties;
import com.hackaton.stock.replenishment.dto.ForecastConfidence;
import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReplenishmentReaderServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private StockMovementRepository movementRepository;
    @Mock
    private PurchaseOrderRepository orderRepository;
    @Mock
    private SupplierRepository supplierRepository;

    private ReplenishmentReaderService service;

    @BeforeEach
    void setUp() {
        ReplenishmentProperties properties = new ReplenishmentProperties(7, 30);
        LeadTimeResolver resolver = new LeadTimeResolver(orderRepository, supplierRepository, properties);
        service = new ReplenishmentReaderService(productRepository,
                new ReplenishmentInputLoader(movementRepository, resolver), new ReplenishmentCalculator(properties));
    }

    @Test
    void shouldForecastFromLastTwentyEightDays_whenHistoryIsLongEnough() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product(1L, "Riz", 5)));
        givenOneOutPerDaySinceThirtyDays(1L);

        ReplenishmentForecast forecast = service.forProduct(1L);

        assertEquals(ForecastConfidence.OK, forecast.confidence());
        assertEquals(1, forecast.averageDailyOut().intValue());
        assertTrue(forecast.needsReorder());
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.forProduct(2L));
    }

    @Test
    void shouldListProductsToOrderFirst_andFilterWhenOnlyNeeded() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Huile", 500), product(2L, "Riz", 5)));
        givenOneOutPerDaySinceThirtyDays(1L);
        givenOneOutPerDaySinceThirtyDays(2L);

        assertEquals(List.of(2L, 1L), service.findAll(false).stream().map(ReplenishmentForecast::productId).toList());
        assertEquals(List.of(2L), service.findAll(true).stream().map(ReplenishmentForecast::productId).toList());
    }

    private void givenOneOutPerDaySinceThirtyDays(Long productId) {
        when(movementRepository.findFirstByProductIdOrderByCreatedAtAsc(productId))
                .thenReturn(Optional.of(unitOut(30)));
        when(movementRepository.findByProductIdAndTypeAndCreatedAtGreaterThanEqual(
                eq(productId), eq(MovementType.OUT), any(Instant.class))).thenReturn(oneUnitOutPerDay(28));
    }
}
