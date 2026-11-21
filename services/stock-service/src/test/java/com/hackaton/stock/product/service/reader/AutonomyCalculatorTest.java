package com.hackaton.stock.product.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class AutonomyCalculatorTest {

    private final AutonomyCalculator calculator = new AutonomyCalculator();

    private Product productWithStock(int quantity) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Riz blanc");
        product.setQuantity(quantity);
        return product;
    }

    @Test
    void shouldReturnOk_whenNothingWasSold() {
        AutonomyResponse response = calculator.calculate(productWithStock(19), 0);

        assertNull(response.daysRemaining());
        assertEquals(StockRisk.OK, response.risk());
    }

    @Test
    void shouldBeCritical_whenStockCoversLessThanThreeDays() {
        AutonomyResponse response = calculator.calculate(productWithStock(19), 70);

        assertEquals(new BigDecimal("10.00"), response.averageDailyOut());
        assertEquals(new BigDecimal("1.9"), response.daysRemaining());
        assertEquals(StockRisk.CRITICAL, response.risk());
    }

    @Test
    void shouldBeWarning_whenStockCoversBetweenThreeAndSevenDays() {
        AutonomyResponse response = calculator.calculate(productWithStock(40), 70);

        assertEquals(new BigDecimal("4.0"), response.daysRemaining());
        assertEquals(StockRisk.WARNING, response.risk());
    }

    @Test
    void shouldBeOk_whenStockCoversAtLeastSevenDays() {
        AutonomyResponse response = calculator.calculate(productWithStock(100), 70);

        assertEquals(new BigDecimal("10.0"), response.daysRemaining());
        assertEquals(StockRisk.OK, response.risk());
    }

    @Test
    void shouldBeCriticalWithZeroDays_whenStockIsEmptyAndProductSells() {
        AutonomyResponse response = calculator.calculate(productWithStock(0), 14);

        assertEquals(new BigDecimal("0.0"), response.daysRemaining());
        assertEquals(StockRisk.CRITICAL, response.risk());
    }
}
