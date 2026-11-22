package com.hackaton.stock.status.service;

import static com.hackaton.stock.product.ProductFixtures.product;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.hackaton.stock.lot.config.ExpiryProperties;
import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.service.expiry.ExpiryPolicy;
import com.hackaton.stock.lot.service.reader.OpenLotReader;
import com.hackaton.stock.movement.dto.ProductOutQuantity;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusQuery;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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
    @Mock
    private OpenLotReader openLotReader;

    private StockStatusReaderService service;

    @BeforeEach
    void setUp() {
        StockStatusItemFactory factory = new StockStatusItemFactory(
                new AutonomyCalculator(), new ExpiryPolicy(Clock.systemUTC(), new ExpiryProperties(30)));
        service = new StockStatusReaderService(productRepository, movementRepository, openLotReader, factory);
    }

    private void givenSales(ProductOutQuantity... sales) {
        when(movementRepository.sumQuantityByProductSince(eq(MovementType.OUT), any(Instant.class)))
                .thenReturn(List.of(sales));
    }

    @Test
    void shouldSortMostUrgentFirst() {
        when(productRepository.findAll()).thenReturn(List.of(
                product(1L, "Sucre", 500, 10), product(2L, "Riz", 19, 5), product(3L, "Lait", 40, 5)));
        givenSales(new ProductOutQuantity(1L, 70L), new ProductOutQuantity(2L, 70L), new ProductOutQuantity(3L, 70L));

        List<StockStatusItem> status = service.findAll(StockStatusQuery.everything());

        assertEquals(List.of("Riz", "Lait", "Sucre"), status.stream().map(StockStatusItem::productName).toList());
        assertEquals(List.of(StockRisk.CRITICAL, StockRisk.WARNING, StockRisk.OK),
                status.stream().map(StockStatusItem::risk).toList());
    }

    @Test
    void shouldKeepOnlyItemsMatchingQuery() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Riz", 19, 5), product(2L, "Lait", 500, 5)));
        givenSales(new ProductOutQuantity(1L, 70L));

        List<StockStatusItem> critical = service.findAll(new StockStatusQuery(StockRisk.CRITICAL, null, null, null));

        assertEquals(List.of("Riz"), critical.stream().map(StockStatusItem::productName).toList());
    }

    @Test
    void shouldAttachEarliestOpenLotOfEachProduct() {
        when(productRepository.findAll()).thenReturn(List.of(product(1L, "Lait", 40, 5)));
        givenSales();
        when(openLotReader.findEarliestOpenLotByProduct()).thenReturn(Map.of(1L, new LotBalance(
                4L, 1L, "LC-2611", LocalDate.now(Clock.systemUTC()).plusDays(10), 48, 0, Instant.now())));

        StockStatusItem item = service.findAll(StockStatusQuery.everything()).get(0);

        assertEquals("LC-2611", item.lotNumber());
        assertEquals(10, item.daysToExpiry());
    }

    @Test
    void shouldReturnEmptyList_whenThereAreNoProducts() {
        when(productRepository.findAll()).thenReturn(List.of());
        givenSales();

        assertTrue(service.findAll(StockStatusQuery.everything()).isEmpty());
    }
}
