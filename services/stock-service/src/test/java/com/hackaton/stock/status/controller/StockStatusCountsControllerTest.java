package com.hackaton.stock.status.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.status.dto.StockStatusCounts;
import com.hackaton.stock.status.service.StockStatusCountsReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockStatusCountsController.class)
class StockStatusCountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockStatusCountsReader countsReader;

    @Test
    void shouldReturnCountsPerFilter() throws Exception {
        when(countsReader.count(null)).thenReturn(new StockStatusCounts(64, 5, 2, 3));

        mockMvc.perform(get("/status/counts").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(64))
                .andExpect(jsonPath("$.alert").value(5))
                .andExpect(jsonPath("$.ruptureRisk").value(2))
                .andExpect(jsonPath("$.expiringSoon").value(3));
    }

    @Test
    void shouldPassSearch_whenSearchIsGiven() throws Exception {
        when(countsReader.count("riz")).thenReturn(new StockStatusCounts(2, 1, 0, 0));

        mockMvc.perform(get("/status/counts").param("search", "riz").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(2));
    }
}
