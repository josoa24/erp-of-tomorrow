package com.hackaton.stock.replenishment.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.replenishment.dto.ForecastConfidence;
import com.hackaton.stock.replenishment.dto.LeadTimeSource;
import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import com.hackaton.stock.replenishment.service.ReplenishmentReader;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReplenishmentController.class)
class ReplenishmentControllerTest {

    private static final ReplenishmentForecast RICE = new ReplenishmentForecast(1L, "Riz", 8, 28,
            new BigDecimal("1.00"), new BigDecimal("1.00"), 4, LeadTimeSource.SUPPLIER, 3L, 4,
            new BigDecimal("8.00"), 30, new BigDecimal("8.0"), true, ForecastConfidence.OK, List.of("Période"));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReplenishmentReader replenishmentReader;

    @Test
    void shouldReturnForecast_whenProductExists() throws Exception {
        when(replenishmentReader.forProduct(1L)).thenReturn(RICE);

        mockMvc.perform(get("/products/1/replenishment").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendedQuantity").value(30))
                .andExpect(jsonPath("$.leadTimeSource").value("SUPPLIER"))
                .andExpect(jsonPath("$.assumptions[0]").value("Période"));
    }

    @Test
    void shouldReturn404_whenProductDoesNotExist() throws Exception {
        when(replenishmentReader.forProduct(9L)).thenThrow(new ResourceNotFoundException("Produit", 9L));

        mockMvc.perform(get("/products/9/replenishment").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldOnlyReturnNeededProducts_byDefault() throws Exception {
        when(replenishmentReader.findAll(true)).thenReturn(List.of(RICE));

        mockMvc.perform(get("/replenishment").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].needsReorder").value(true));
        verify(replenishmentReader).findAll(true);
    }

    @Test
    void shouldPassFilter_whenOnlyNeededIsFalse() throws Exception {
        when(replenishmentReader.findAll(false)).thenReturn(List.of(RICE));

        mockMvc.perform(get("/replenishment").param("onlyNeeded", "false").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
        verify(replenishmentReader).findAll(false);
    }

    @Test
    void shouldReturn400_whenParameterIsInvalid() throws Exception {
        mockMvc.perform(get("/replenishment").param("onlyNeeded", "peut-etre").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
