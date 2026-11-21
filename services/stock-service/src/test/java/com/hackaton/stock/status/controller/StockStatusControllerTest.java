package com.hackaton.stock.status.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.service.StockStatusReader;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockStatusController.class)
class StockStatusControllerTest {

    private static final StockStatusItem RICE = new StockStatusItem(1L, "Riz", "RIZ-01", "kg", 2, 5, true,
            new BigDecimal("1.00"), new BigDecimal("2.00"), StockRisk.CRITICAL);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockStatusReader statusReader;

    @Test
    void shouldReturnAllItems_whenRiskIsAbsent() throws Exception {
        when(statusReader.findAll(null)).thenReturn(List.of(RICE));

        mockMvc.perform(get("/status").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].risk").value("CRITICAL"));
    }

    @Test
    void shouldPassRiskFilter_whenRiskIsGiven() throws Exception {
        when(statusReader.findAll(StockRisk.CRITICAL)).thenReturn(List.of(RICE));

        mockMvc.perform(get("/status").param("risk", "CRITICAL").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(statusReader).findAll(StockRisk.CRITICAL);
    }

    @Test
    void shouldReturn400_whenRiskIsUnknown() throws Exception {
        mockMvc.perform(get("/status").param("risk", "UNKNOWN").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
