package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.sale;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.service.creator.SaleCreator;
import com.hackaton.vente.sale.service.deleter.SaleDeleter;
import com.hackaton.vente.sale.service.reader.SaleReader;
import com.hackaton.vente.sale.service.updater.SaleUpdater;
import com.hackaton.vente.sale.service.validator.SaleValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerReadTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleReader saleReader;

    @MockitoBean
    private SaleCreator saleCreator;

    @MockitoBean
    private SaleUpdater saleUpdater;

    @MockitoBean
    private SaleDeleter saleDeleter;

    @MockitoBean
    private SaleValidator saleValidator;

    @Test
    void shouldReturnSaleWithLines_whenSaleExists() throws Exception {
        when(saleReader.findById(5L)).thenReturn(sale(5L, DocumentStatus.VALIDATED));

        mockMvc.perform(get("/sales/5").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount").value(5000))
                .andExpect(jsonPath("$.lines[0].productName").value("Riz blanc"));
    }

    @Test
    void shouldReturn404_whenSaleDoesNotExist() throws Exception {
        when(saleReader.findById(9L)).thenThrow(new ResourceNotFoundException("Vente", 9L));

        mockMvc.perform(get("/sales/9").with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
