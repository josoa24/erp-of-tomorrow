package com.hackaton.stock.product.repository;

import static com.hackaton.stock.product.ProductFixtures.product;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ProductRepository productRepository;

    private void persist(String name, int quantity, String unitPrice, String purchasePrice) {
        Product product = product(null, name, quantity, 5);
        product.setSku(name);
        product.setUnitPrice(new BigDecimal(unitPrice));
        product.setPurchasePrice(purchasePrice == null ? null : new BigDecimal(purchasePrice));
        entityManager.persist(product);
    }

    @BeforeEach
    void setUp() {
        persist("RIZ-50", 40, "165000", "142000");
        persist("HUI-5", 4, "42000", null);
        persist("BOUG-12", 10, "3000", "2000");
        entityManager.flush();
    }

    @Test
    void shouldValueStockAtPurchasePriceFallingBackToUnitPrice() {
        assertEquals(0, new BigDecimal("5868000").compareTo(productRepository.totalPurchaseValue()));
    }

    @Test
    void shouldCountProductsWithoutPurchasePrice() {
        assertEquals(1L, productRepository.countByPurchasePriceIsNull());
    }
}
