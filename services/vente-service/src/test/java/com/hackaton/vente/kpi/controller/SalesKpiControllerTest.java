package com.hackaton.vente.kpi.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.kpi.dto.CreditSummaryResponse;
import com.hackaton.vente.kpi.dto.CustomerCreditResponse;
import com.hackaton.vente.kpi.dto.SalesKpiResponse;
import com.hackaton.vente.kpi.service.credit.CreditReader;
import com.hackaton.vente.kpi.service.sales.SalesKpiReader;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SalesKpiController.class)
class SalesKpiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SalesKpiReader kpiReader;
    @MockitoBean
    private CreditReader creditReader;

    @Test
    void shouldReturnKpis() throws Exception {
        when(kpiReader.read()).thenReturn(new SalesKpiResponse(new BigDecimal("18450000"), new BigDecimal("16590000"),
                23, new BigDecimal("1300000"), new BigDecimal("1240000"), new BigDecimal("1200000"),
                new BigDecimal("80200"), new BigDecimal("77100")));

        mockMvc.perform(get("/sales/kpis").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthRevenue").value(18450000))
                .andExpect(jsonPath("$.todaySalesCount").value(23))
                .andExpect(jsonPath("$.todayCollected").value(1240000));
    }

    @Test
    void shouldReturnCreditSummary() throws Exception {
        when(creditReader.summarize()).thenReturn(new CreditSummaryResponse(new BigDecimal("2300000"), 8, 2));

        mockMvc.perform(get("/sales/credit-summary").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.outstanding").value(2300000))
                .andExpect(jsonPath("$.customers").value(8))
                .andExpect(jsonPath("$.overdueCustomers").value(2));
    }

    @Test
    void shouldReturnCreditByCustomer() throws Exception {
        when(creditReader.byCustomer())
                .thenReturn(List.of(new CustomerCreditResponse(7L, new BigDecimal("760000"), 1)));

        mockMvc.perform(get("/sales/credit-by-customer").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(7))
                .andExpect(jsonPath("$[0].overdueCount").value(1));
    }
}
