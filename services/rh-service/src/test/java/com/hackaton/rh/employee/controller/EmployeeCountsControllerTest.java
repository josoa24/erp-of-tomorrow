package com.hackaton.rh.employee.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.employee.dto.EmployeeCountsResponse;
import com.hackaton.rh.employee.service.counts.EmployeeCountReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(EmployeeCountsController.class)
class EmployeeCountsControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private EmployeeCountReader countReader;

    @Test
    void shouldReturn200WithCounts_whenCountingEmployees() throws Exception {
        when(countReader.count()).thenReturn(new EmployeeCountsResponse(37, 34, 3, 2));

        mockMvc.perform(get("/employees/counts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(37))
                .andExpect(jsonPath("$.present").value(34))
                .andExpect(jsonPath("$.onLeave").value(3))
                .andExpect(jsonPath("$.advanceRequested").value(2));
    }
}
