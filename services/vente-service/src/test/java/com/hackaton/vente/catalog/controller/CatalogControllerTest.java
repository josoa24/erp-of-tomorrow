package com.hackaton.vente.catalog.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.catalog.dto.CatalogProductResponse;
import com.hackaton.vente.catalog.service.CatalogReader;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CatalogController.class)
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CatalogReader catalogReader;

    @Test
    void shouldReturnCatalog_whenStockAnswers() throws Exception {
        when(catalogReader.search("huile")).thenReturn(List.of(
                new CatalogProductResponse(12L, "Huile 5 L", "HUI-5", "bidon", new BigDecimal("42000"), 4)));

        mockMvc.perform(get("/catalog/products").param("search", "huile").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sku").value("HUI-5"))
                .andExpect(jsonPath("$[0].unit").value("bidon"))
                .andExpect(jsonPath("$[0].quantity").value(4));
    }

    @Test
    void shouldReturn503_whenStockIsDown() throws Exception {
        when(catalogReader.search(null))
                .thenThrow(new ServiceUnavailableException("Le service Stock est indisponible"));

        mockMvc.perform(get("/catalog/products").with(asAdmin()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
