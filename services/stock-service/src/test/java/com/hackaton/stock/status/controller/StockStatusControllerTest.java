package com.hackaton.stock.status.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static com.hackaton.stock.status.StatusItemFixtures.item;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusQuery;
import com.hackaton.stock.status.service.StockStatusReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockStatusController.class)
class StockStatusControllerTest {

    private static final StockStatusItem OIL = item(1L, "Huile 5 L", "Épicerie", StockRisk.CRITICAL, true, 23);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockStatusReader statusReader;

    @Test
    void shouldReturnAllItems_whenNoFilterIsGiven() throws Exception {
        when(statusReader.findAll(StockStatusQuery.everything())).thenReturn(List.of(OIL));

        mockMvc.perform(get("/status").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].risk").value("CRITICAL"))
                .andExpect(jsonPath("$[0].category").value("Épicerie"))
                .andExpect(jsonPath("$[0].statusLabel").value("RUPTURE_RISK"))
                .andExpect(jsonPath("$[0].lotNumber").value("L-1"))
                .andExpect(jsonPath("$[0].expiryDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].daysToExpiry").value(23))
                .andExpect(jsonPath("$[0].expiringSoon").value(true));
    }

    @Test
    void shouldPassRiskFilter_whenRiskIsGiven() throws Exception {
        StockStatusQuery critical = new StockStatusQuery(StockRisk.CRITICAL, null, null, null);
        when(statusReader.findAll(critical)).thenReturn(List.of(OIL));

        mockMvc.perform(get("/status").param("risk", "CRITICAL").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(statusReader).findAll(critical);
    }

    @Test
    void shouldPassSearchLowStockAndExpiryFilters() throws Exception {
        StockStatusQuery query = new StockStatusQuery(null, "huile", true, 30);
        when(statusReader.findAll(query)).thenReturn(List.of(OIL));

        mockMvc.perform(get("/status").headers(admin())
                        .param("search", "huile").param("lowStock", "true").param("expiringWithinDays", "30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Huile 5 L"));
        verify(statusReader).findAll(query);
    }

    @Test
    void shouldReturn400_whenRiskIsUnknown() throws Exception {
        mockMvc.perform(get("/status").param("risk", "UNKNOWN").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenExpiryWindowIsNotANumber() throws Exception {
        mockMvc.perform(get("/status").param("expiringWithinDays", "bientot").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
