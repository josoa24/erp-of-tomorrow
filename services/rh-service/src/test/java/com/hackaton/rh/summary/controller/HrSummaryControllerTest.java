package com.hackaton.rh.summary.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.summary.dto.DepartmentHeadcount;
import com.hackaton.rh.summary.dto.HrSummaryResponse;
import com.hackaton.rh.summary.service.HrSummaryReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(HrSummaryController.class)
class HrSummaryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private HrSummaryReader summaryReader;

    @Test
    void shouldReturn200WithHeadcounts_whenSummarizing() throws Exception {
        HrSummaryResponse summary =
                new HrSummaryResponse(12, 2, List.of(new DepartmentHeadcount(3L, "Finance", 5L)), 3, 1);
        when(summaryReader.summarize()).thenReturn(summary);

        mockMvc.perform(get("/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees").value(12))
                .andExpect(jsonPath("$.onLeaveToday").value(2))
                .andExpect(jsonPath("$.headcountByDepartment[0].departmentName").value("Finance"))
                .andExpect(jsonPath("$.headcountByDepartment[0].employeeCount").value(5))
                .andExpect(jsonPath("$.pendingLeaves").value(3))
                .andExpect(jsonPath("$.departmentsWithEmployees").value(1));
    }
}
