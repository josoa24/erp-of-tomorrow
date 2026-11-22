package com.hackaton.module.salesimport.controller;

import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.salesimport.dto.SalesImportAvailability;
import com.hackaton.module.salesimport.dto.SalesImportResult;
import com.hackaton.module.salesimport.service.SalesImportCreator;
import com.hackaton.module.salesimport.service.SalesImportReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SalesImportController.class)
class SalesImportControllerTest {

    private static final String URL = "/definitions/livraisons/sales-import";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SalesImportReader importReader;
    @MockitoBean
    private SalesImportCreator importCreator;

    @Test
    void shouldReturnAvailableCount() throws Exception {
        when(importReader.availability("livraisons")).thenReturn(new SalesImportAvailability(3));

        mockMvc.perform(asSeller(get(URL))).andExpect(status().isOk()).andExpect(jsonPath("$.available").value(3));
    }

    @Test
    void shouldReturnCreatedCount_whenImporting() throws Exception {
        when(importCreator.importSales(eq("livraisons"), any())).thenReturn(new SalesImportResult(2));

        mockMvc.perform(asSeller(post(URL))).andExpect(status().isOk()).andExpect(jsonPath("$.created").value(2));
    }

    @Test
    void shouldReturn422_whenModuleHasNoSaleColumn() throws Exception {
        when(importReader.availability("livraisons")).thenThrow(new BusinessException("Pas de colonne vente"));

        mockMvc.perform(asSeller(get(URL)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
