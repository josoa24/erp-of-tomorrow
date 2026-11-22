package com.hackaton.stock.product.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ProductMapperTest {

    private final ProductMapper mapper = new ProductMapper();

    private ProductCreateRequest createRequest(String category, BigDecimal purchasePrice) {
        return new ProductCreateRequest("Riz makalioka", "RIZ-50", "sac", 40, 10,
                new BigDecimal("165000"), category, purchasePrice);
    }

    @Test
    void shouldCopyCategoryAndPurchasePrice_whenCreatingEntity() {
        Product product = mapper.toEntity(createRequest("Épicerie", new BigDecimal("142000")));

        assertEquals("Épicerie", product.getCategory());
        assertEquals(new BigDecimal("142000"), product.getPurchasePrice());
    }

    @Test
    void shouldTrimCategory_whenCategoryHasSurroundingSpaces() {
        Product product = mapper.toEntity(createRequest("  Boissons ", null));

        assertEquals("Boissons", product.getCategory());
    }

    @Test
    void shouldStoreNullCategory_whenCategoryIsBlank() {
        Product product = mapper.toEntity(createRequest("   ", null));

        assertNull(product.getCategory());
        assertNull(product.getPurchasePrice());
    }

    @Test
    void shouldReplaceCategoryAndPurchasePrice_whenUpdating() {
        Product product = mapper.toEntity(createRequest("Épicerie", new BigDecimal("142000")));

        mapper.applyUpdate(product, new ProductUpdateRequest("Riz", "sac", 8, new BigDecimal("170000"), null, null));

        assertNull(product.getCategory());
        assertNull(product.getPurchasePrice());
    }

    @Test
    void shouldExposeCategoryAndPurchasePrice_whenMappingResponse() {
        Product product = mapper.toEntity(createRequest("Hygiène", new BigDecimal("3500")));

        ProductResponse response = mapper.toResponse(product);

        assertEquals("Hygiène", response.category());
        assertEquals(new BigDecimal("3500"), response.purchasePrice());
    }
}
