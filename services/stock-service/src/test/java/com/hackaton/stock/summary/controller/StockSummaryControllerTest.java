package com.hackaton.stock.summary.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.summary.dto.StockSummaryResponse;
import com.hackaton.stock.summary.service.reader.StockSummaryReader;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockSummaryController.class)
class StockSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockSummaryReader summaryReader;

    @Test
    void shouldReturnSummary() throws Exception {
        when(summaryReader.summarize()).thenReturn(new StockSummaryResponse(12, 3, new BigDecimal("1500.50")));

        mockMvc.perform(get("/summary").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(12))
                .andExpect(jsonPath("$.lowStockProducts").value(3))
                .andExpect(jsonPath("$.totalStockValue").value(1500.50));
    }
}
