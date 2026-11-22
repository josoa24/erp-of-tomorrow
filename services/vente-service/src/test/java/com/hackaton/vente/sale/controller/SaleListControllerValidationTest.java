package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.sale.service.counts.SaleCountsReader;
import com.hackaton.vente.sale.service.reader.SaleReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleListController.class)
class SaleListControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleReader saleReader;

    @MockitoBean
    private SaleCountsReader countsReader;

    @Test
    void shouldReturn400_whenStatusIsUnknown() throws Exception {
        mockMvc.perform(get("/sales").param("status", "CANCELLED").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenPaymentStatusIsUnknown() throws Exception {
        mockMvc.perform(get("/sales").param("paymentStatus", "LATE").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenPaymentMethodIsUnknown() throws Exception {
        mockMvc.perform(get("/sales").param("paymentMethod", "CHEQUE").with(asAdmin()))
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
    void shouldReturn400_whenCountsDateIsMalformed() throws Exception {
        mockMvc.perform(get("/sales/counts").param("to", "30-11-2026").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
