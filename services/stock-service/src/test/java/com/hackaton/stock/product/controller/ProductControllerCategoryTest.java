package com.hackaton.stock.product.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
import com.hackaton.stock.product.service.creator.ProductCreator;
import com.hackaton.stock.product.service.deleter.ProductDeleter;
import com.hackaton.stock.product.service.reader.ProductReader;
import com.hackaton.stock.product.service.updater.ProductUpdater;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerCategoryTest {

    private static final ProductResponse OIL = new ProductResponse(2L, "Huile 5 L", "HUI-5", "bidon", 4, 10,
            new BigDecimal("42000"), true, Instant.now(), "Épicerie", new BigDecimal("36500"));

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
    void shouldBindCategoryAndPurchasePrice_whenCreating() throws Exception {
        when(productCreator.create(any(ProductCreateRequest.class))).thenReturn(OIL);

        mockMvc.perform(post("/products").headers(admin()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Huile 5 L","sku":"HUI-5","unit":"bidon","quantity":4,"minThreshold":10,
                         "unitPrice":42000,"category":"Épicerie","purchasePrice":36500}"""))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.category").value("Épicerie"))
                .andExpect(jsonPath("$.purchasePrice").value(36500));
        ArgumentCaptor<ProductCreateRequest> captor = ArgumentCaptor.forClass(ProductCreateRequest.class);
        verify(productCreator).create(captor.capture());
        assertEquals("Épicerie", captor.getValue().category());
        assertEquals(new BigDecimal("36500"), captor.getValue().purchasePrice());
    }

    @Test
    void shouldReturn400_whenCategoryIsLongerThan80Characters() throws Exception {
        mockMvc.perform(post("/products").headers(admin()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Riz","sku":"RIZ-01","unit":"sac","quantity":1,"minThreshold":1,"unitPrice":1,
                         "category":"%s"}""".formatted("x".repeat(81))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenPurchasePriceIsNegative() throws Exception {
        mockMvc.perform(put("/products/2").headers(admin()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Huile","unit":"bidon","minThreshold":10,"unitPrice":42000,"purchasePrice":-1}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldAcceptUpdateWithoutCategory_whenFieldsAreOmitted() throws Exception {
        when(productUpdater.update(eq(2L), any(ProductUpdateRequest.class))).thenReturn(OIL);

        mockMvc.perform(put("/products/2").headers(admin()).contentType(MediaType.APPLICATION_JSON).content("""
                        {"name":"Huile 5 L","unit":"bidon","minThreshold":10,"unitPrice":42000}"""))
                .andExpect(status().isOk());
    }
}
