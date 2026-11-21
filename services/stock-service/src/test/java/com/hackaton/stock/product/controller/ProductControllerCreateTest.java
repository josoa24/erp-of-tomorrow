package com.hackaton.stock.product.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.service.creator.ProductCreator;
import com.hackaton.stock.product.service.deleter.ProductDeleter;
import com.hackaton.stock.product.service.reader.ProductReader;
import com.hackaton.stock.product.service.updater.ProductUpdater;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(ProductController.class)
class ProductControllerCreateTest {

    private static final String VALID_BODY = """
            {"name":"Riz","sku":"RIZ-01","unit":"kg","quantity":10,"minThreshold":5,"unitPrice":2.5}""";

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

    private MockHttpServletRequestBuilder createRequest(String body) {
        return post("/products").headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturn201_whenProductIsCreated() throws Exception {
        when(productCreator.create(any(ProductCreateRequest.class))).thenReturn(new ProductResponse(
                1L, "Riz", "RIZ-01", "kg", 10, 5, new BigDecimal("2.5"), false, Instant.now()));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.sku").value("RIZ-01"));
    }

    @Test
    void shouldReturn400_whenRequiredFieldsAreMissing() throws Exception {
        mockMvc.perform(createRequest("""
                        {"name":"","unit":"kg","quantity":-1,"minThreshold":5}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenBodyIsMalformed() throws Exception {
        mockMvc.perform(createRequest("{not json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn409_whenSkuAlreadyExists() throws Exception {
        when(productCreator.create(any(ProductCreateRequest.class)))
                .thenThrow(new DuplicateResourceException("Un produit avec le SKU RIZ-01 existe déjà"));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
