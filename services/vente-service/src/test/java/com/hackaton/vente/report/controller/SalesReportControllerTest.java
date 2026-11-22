package com.hackaton.vente.report.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.report.dto.DailyRevenue;
import com.hackaton.vente.report.dto.SalesSummaryResponse;
import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.report.service.SalesSummaryReader;
import com.hackaton.vente.report.service.TopProductsReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SalesReportController.class)
class SalesReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SalesSummaryReader summaryReader;

    @MockitoBean
    private TopProductsReader topProductsReader;

    @Test
    void shouldReturnSummary_whenPeriodIsValid() throws Exception {
        LocalDate day = LocalDate.of(2026, 11, 21);
        DailyRevenue daily = new DailyRevenue(day, 2, new BigDecimal("10000"));
        when(summaryReader.summarize(day, day)).thenReturn(
                new SalesSummaryResponse(2, new BigDecimal("10000"), new BigDecimal("5000"), List.of(daily)));

        mockMvc.perform(get("/sales/summary").param("from", "2026-11-21").param("to", "2026-11-21").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.salesCount").value(2))
                .andExpect(jsonPath("$.averageBasket").value(5000))
                .andExpect(jsonPath("$.daily[0].date").value("2026-11-21"));
    }

    @Test
    void shouldReturn400_whenSummaryDateIsMalformed() throws Exception {
        mockMvc.perform(get("/sales/summary").param("to", "2026-13-40").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldUseDefaultLimitOfFive_whenLimitIsMissing() throws Exception {
        when(topProductsReader.findTop(5)).thenReturn(
                List.of(new TopProductResponse(10L, "Riz blanc", 40, new BigDecimal("100000"))));

        mockMvc.perform(get("/sales/top-products").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Riz blanc"))
                .andExpect(jsonPath("$[0].totalQuantity").value(40));
    }

    @Test
    void shouldDelegateClampingToReader_whenLimitIsOutOfBounds() throws Exception {
        when(topProductsReader.findTop(500)).thenReturn(List.of());

        mockMvc.perform(get("/sales/top-products").param("limit", "500").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(topProductsReader).findTop(500);
    }

    @Test
    void shouldReturn400_whenLimitIsNotANumber() throws Exception {
        mockMvc.perform(get("/sales/top-products").param("limit", "abc").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
