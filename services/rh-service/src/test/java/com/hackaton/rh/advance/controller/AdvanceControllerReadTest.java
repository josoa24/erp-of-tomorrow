package com.hackaton.rh.advance.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.advance.dto.AdvanceSummaryResponse;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.service.creator.AdvanceCreator;
import com.hackaton.rh.advance.service.reader.AdvanceReader;
import com.hackaton.rh.advance.service.summary.AdvanceSummaryReader;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdvanceController.class)
class AdvanceControllerReadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdvanceReader advanceReader;
    @MockitoBean
    private AdvanceSummaryReader summaryReader;
    @MockitoBean
    private AdvanceCreator advanceCreator;

    @Test
    void shouldReturn200WithAdvances_whenSearchingByMonthEmployeeAndStatus() throws Exception {
        when(advanceReader.search(YearMonth.of(2026, 11), 7L, AdvanceStatus.DRAFT))
                .thenReturn(List.of(AdvanceFixtures.advance(AdvanceStatus.DRAFT)));

        mockMvc.perform(get("/advances").param("month", "2026-11").param("employeeId", "7").param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].month").value("2026-11"))
                .andExpect(jsonPath("$[0].employeeName").value("Nirina Rabe"))
                .andExpect(jsonPath("$[0].amount").value(80000));
    }

    @Test
    void shouldReturn400_whenMonthIsMalformed() throws Exception {
        mockMvc.perform(get("/advances").param("month", "2026-13"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(advanceReader);
    }

    @Test
    void shouldReturn200WithMonthTotals_whenSummarizing() throws Exception {
        when(summaryReader.summarize()).thenReturn(new AdvanceSummaryResponse(new BigDecimal("1200000"), 6, 2));

        mockMvc.perform(get("/advances/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.monthTotal").value(1200000))
                .andExpect(jsonPath("$.requests").value(6))
                .andExpect(jsonPath("$.pending").value(2));
    }
}
