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
import java.time.LocalDate;
import java.util.List;
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
    void shouldReturnFilteredSales_whenFiltersAreValid() throws Exception {
        LocalDate from = LocalDate.of(2026, 11, 1);
        LocalDate to = LocalDate.of(2026, 11, 30);
        when(saleReader.search(1L, DocumentStatus.DRAFT, from, to)).thenReturn(List.of(sale(5L, DocumentStatus.DRAFT)));

        mockMvc.perform(get("/sales").param("customerId", "1").param("status", "DRAFT")
                        .param("from", "2026-11-01").param("to", "2026-11-30").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenStatusIsUnknown() throws Exception {
        mockMvc.perform(get("/sales").param("status", "CANCELLED").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenDateIsMalformed() throws Exception {
        mockMvc.perform(get("/sales").param("from", "01/11/2026").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

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
