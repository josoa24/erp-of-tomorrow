package com.hackaton.stock.product.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.service.creator.ProductCreator;
import com.hackaton.stock.product.service.deleter.ProductDeleter;
import com.hackaton.stock.product.service.reader.ProductReader;
import com.hackaton.stock.product.service.updater.ProductUpdater;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerReadTest {

    private static final ProductResponse RICE = new ProductResponse(
            1L, "Riz", "RIZ-01", "kg", 3, 5, BigDecimal.TEN, true, Instant.now(), null, null);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductReader productReader;
    @MockitoBean
    private ProductCreator productCreator;
    @MockitoBean
    private ProductUpdater productUpdater;
    @MockitoBean
    private ProductDeleter productDeleter;

    @Test
    void shouldReturnProducts_whenSearchingWithFilters() throws Exception {
        when(productReader.search("riz", true)).thenReturn(List.of(RICE));

        mockMvc.perform(get("/products").param("search", "riz").param("lowStock", "true").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("RIZ-01"));
        verify(productReader).search("riz", true);
    }

    @Test
    void shouldReturn400_whenLowStockIsNotBoolean() throws Exception {
        mockMvc.perform(get("/products").param("lowStock", "maybe").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturnLowStockProducts() throws Exception {
        when(productReader.findLowStock()).thenReturn(List.of(RICE));

        mockMvc.perform(get("/products/low-stock").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lowStock").value(true));
    }

    @Test
    void shouldReturnProduct_whenProductExists() throws Exception {
        when(productReader.findById(1L)).thenReturn(RICE);

        mockMvc.perform(get("/products/1").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Riz"));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(productReader.findById(9L)).thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(get("/products/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn400_whenIdIsNotNumeric() throws Exception {
        mockMvc.perform(get("/products/abc").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
