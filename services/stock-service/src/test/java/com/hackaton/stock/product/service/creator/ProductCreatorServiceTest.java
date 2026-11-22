package com.hackaton.stock.product.service.creator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.mapper.ProductMapper;
import com.hackaton.stock.product.repository.ProductRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCreatorServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductCreatorService service;
    private final ProductCreateRequest request =
            new ProductCreateRequest("Riz blanc", "RIZ-001", "sac", 19, 5, new BigDecimal("45000"), null, null);

    @BeforeEach
    void setUp() {
        service = new ProductCreatorService(productRepository, new ProductMapper());
    }

    @Test
    void shouldCreateProduct_whenSkuIsNew() {
        when(productRepository.existsBySku("RIZ-001")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(call -> call.getArgument(0));

        ProductResponse response = service.create(request);

        assertEquals("RIZ-001", response.sku());
        assertEquals(19, response.quantity());
    }

    @Test
    void shouldThrowDuplicate_whenSkuAlreadyExists() {
        when(productRepository.existsBySku("RIZ-001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.create(request));
        verify(productRepository, never()).save(any());
    }
}
