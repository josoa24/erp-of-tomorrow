package com.hackaton.stock.product;

import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;

public final class ProductFixtures {

    private ProductFixtures() {
    }

    public static Product product(Long id, String name, int quantity, int threshold) {
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
}
