package com.hackaton.stock.product.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.service.reader.ProductAutonomyReader;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductAutonomyController.class)
class ProductAutonomyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductAutonomyReader autonomyReader;

    @Test
    void shouldReturnAutonomy_whenProductExists() throws Exception {
        when(autonomyReader.analyze(1L)).thenReturn(new AutonomyResponse(
                1L, "Riz", 14, 7, new BigDecimal("2.00"), new BigDecimal("7.00"), StockRisk.WARNING));

        mockMvc.perform(get("/products/1/autonomy").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.daysRemaining").value(7.00))
                .andExpect(jsonPath("$.risk").value("WARNING"));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(autonomyReader.analyze(9L)).thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(get("/products/9/autonomy").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
