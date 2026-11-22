package com.hackaton.stock.lot.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.lot.dto.LotResponse;
import com.hackaton.stock.lot.service.reader.StockLotReader;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StockLotController.class)
class StockLotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StockLotReader lotReader;

    @Test
    void shouldReturnProductLots() throws Exception {
        when(lotReader.findByProductId(3L)).thenReturn(List.of(new LotResponse(
                "LC-2611", LocalDate.of(2026, 12, 15), 48, 18, Instant.parse("2026-11-02T07:00:00Z"))));

        mockMvc.perform(get("/products/3/lots").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].lotNumber").value("LC-2611"))
                .andExpect(jsonPath("$[0].expiryDate").value("2026-12-15"))
                .andExpect(jsonPath("$[0].quantity").value(48))
                .andExpect(jsonPath("$[0].remaining").value(18))
                .andExpect(jsonPath("$[0].receivedAt").value("2026-11-02T07:00:00Z"));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(lotReader.findByProductId(9L)).thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(get("/products/9/lots").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn401_whenGatewayIdentityIsMissing() throws Exception {
        mockMvc.perform(get("/products/3/lots"))
                .andExpect(status().isUnauthorized());
    }
}
