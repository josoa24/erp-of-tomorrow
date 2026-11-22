package com.hackaton.stock.lot.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.entity.StockLot;
import com.hackaton.stock.lot.entity.StockLotAllocation;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class StockLotRepositoryTest {

    private static final LocalDate DECEMBER = LocalDate.of(2026, 12, 15);

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private StockLotRepository lotRepository;
    @Autowired
    private ProductRepository productRepository;

    private Product milk;
    private StockLot early;

    private StockMovement movement(MovementType type, int quantity) {
        StockMovement movement = new StockMovement();
        movement.setProductId(milk.getId());
        movement.setType(type);
        movement.setQuantity(quantity);
        movement.setStockAfter(quantity);
        return entityManager.persist(movement);
    }

    private StockLot lot(String number, LocalDate expiry, int quantity) {
        return entityManager.persist(new StockLot(milk, movement(MovementType.IN, quantity), number, expiry,
                Instant.parse("2026-11-02T07:00:00Z")));
    }

    @BeforeEach
    void setUp() {
        milk = new Product();
        milk.setName("Lait concentré");
        milk.setSku("LAIT-397");
        milk.setUnit("boîte");
        milk.setQuantity(60);
        milk.setMinThreshold(12);
        milk.setUnitPrice(new BigDecimal("4500"));
        entityManager.persist(milk);
        lot("LC-LATE", DECEMBER.plusDays(40), 30);
        early = lot("LC-EARLY", DECEMBER, 10);
        StockMovement exit = movement(MovementType.OUT, 10);
        entityManager.persist(new StockLotAllocation(early, exit, 6));
        entityManager.persist(new StockLotAllocation(early, exit, 4));
        entityManager.flush();
    }

    @Test
    void shouldReturnBalancesInFefoOrder_whenReadingProductLots() {
        List<LotBalance> balances = lotRepository.findBalancesByProductId(milk.getId());

        assertEquals(List.of("LC-EARLY", "LC-LATE"), balances.stream().map(LotBalance::lotNumber).toList());
        assertEquals(10L, balances.get(0).allocated());
        assertEquals(0L, balances.get(0).remaining());
        assertEquals(30L, balances.get(1).remaining());
    }

    @Test
    void shouldExcludeExhaustedLots_whenReadingOpenBalances() {
        List<LotBalance> open = lotRepository.findOpenBalances();

        assertEquals(List.of("LC-LATE"), open.stream().map(LotBalance::lotNumber).toList());
    }

    @Test
    void shouldDetectExistingLotNumberPerProduct() {
        assertTrue(lotRepository.existsByProductIdAndLotNumber(milk.getId(), "LC-EARLY"));
        assertFalse(lotRepository.existsByProductIdAndLotNumber(milk.getId(), "LC-OTHER"));
    }

    @Test
    void shouldLoadProductWithWriteLock() {
        assertEquals("LAIT-397", productRepository.findForUpdateById(milk.getId()).orElseThrow().getSku());
    }
}
