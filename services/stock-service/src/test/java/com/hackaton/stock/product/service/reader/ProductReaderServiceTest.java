package com.hackaton.stock.product.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.mapper.ProductMapper;
import com.hackaton.stock.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductReaderServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductReaderService service;
    private Product rice;
    private Product milk;

    private Product product(String name, int quantity, int threshold) {
        Product product = new Product();
        product.setName(name);
        product.setSku(name.toUpperCase());
        product.setQuantity(quantity);
        product.setMinThreshold(threshold);
        product.setUnitPrice(BigDecimal.TEN);
        return product;
    }

    @BeforeEach
    void setUp() {
        service = new ProductReaderService(productRepository, new ProductMapper());
        rice = product("Riz", 3, 5);
        milk = product("Lait", 50, 5);
    }

    @Test
    void shouldReturnAllMatches_whenLowStockFilterIsAbsent() {
        when(productRepository.search("%%")).thenReturn(List.of(milk, rice));

        assertEquals(2, service.search(null, null).size());
    }

    @Test
    void shouldKeepOnlyLowStock_whenFilterIsTrue() {
        when(productRepository.search("%%")).thenReturn(List.of(milk, rice));

        List<ProductResponse> result = service.search(null, true);

        assertEquals(1, result.size());
        assertEquals("Riz", result.get(0).name());
    }

    @Test
    void shouldKeepOnlyHealthyStock_whenFilterIsFalse() {
        when(productRepository.search("%%")).thenReturn(List.of(milk, rice));

        List<ProductResponse> result = service.search("  ", false);

        assertEquals(1, result.size());
        assertEquals("Lait", result.get(0).name());
    }

    @Test
    void shouldBuildLikePattern_fromTrimmedSearch() {
        when(productRepository.search("%riz%")).thenReturn(List.of(rice));

        assertEquals(1, service.search(" riz ", null).size());
    }

    @Test
    void shouldReturnLowStockProducts() {
        when(productRepository.findLowStock()).thenReturn(List.of(rice));

        assertEquals(true, service.findLowStock().get(0).lowStock());
    }

    @Test
    void shouldThrowNotFound_whenProductDoesNotExist() {
        when(productRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }
}
