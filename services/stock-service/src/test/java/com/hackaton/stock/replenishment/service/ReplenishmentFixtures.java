package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.movement.entity.StockMovement;
import com.hackaton.stock.product.entity.Product;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

final class ReplenishmentFixtures {

    private ReplenishmentFixtures() {
    }

    static Product product(Long id, String name, int quantity) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setQuantity(quantity);
        return product;
    }

    static StockMovement unitOut(int daysAgo) {
        StockMovement out = new StockMovement();
        out.setCreatedAt(Instant.now().minus(daysAgo, ChronoUnit.DAYS));
        out.setQuantity(1);
        return out;
    }

    static List<StockMovement> oneUnitOutPerDay(int days) {
        return IntStream.range(0, days).mapToObj(ReplenishmentFixtures::unitOut).toList();
    }
}
