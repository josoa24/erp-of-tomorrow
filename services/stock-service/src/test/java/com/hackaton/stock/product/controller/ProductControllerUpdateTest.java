package com.hackaton.stock.product.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
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
class ProductControllerUpdateTest {

    private static final String VALID_BODY = """
            {"name":"Riz long","unit":"kg","minThreshold":8,"unitPrice":3}""";

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

    private MockHttpServletRequestBuilder updateRequest(long id, String body) {
        return put("/products/" + id).headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturnUpdatedProduct_whenUpdateIsValid() throws Exception {
        when(productUpdater.update(eq(1L), any(ProductUpdateRequest.class))).thenReturn(new ProductResponse(
                1L, "Riz long", "RIZ-01", "kg", 10, 8, new BigDecimal("3"), false, Instant.now(), null, null));

        mockMvc.perform(updateRequest(1L, VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Riz long"))
                .andExpect(jsonPath("$.minThreshold").value(8));
    }

    @Test
    void shouldReturn400_whenUnitPriceIsMissing() throws Exception {
        mockMvc.perform(updateRequest(1L, """
                        {"name":"Riz","unit":"kg","minThreshold":8}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenUpdatingUnknownProduct() throws Exception {
        when(productUpdater.update(eq(9L), any(ProductUpdateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(updateRequest(9L, VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn204_whenProductIsDeleted() throws Exception {
        mockMvc.perform(delete("/products/1").headers(admin()))
                .andExpect(status().isNoContent());
        verify(productDeleter).delete(1L);
    }

    @Test
    void shouldReturn404_whenDeletingUnknownProduct() throws Exception {
        doThrow(new ResourceNotFoundException("Produit", 9L)).when(productDeleter).delete(9L);

        mockMvc.perform(delete("/products/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
