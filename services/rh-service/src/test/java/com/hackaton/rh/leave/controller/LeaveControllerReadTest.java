package com.hackaton.rh.leave.controller;

import static com.hackaton.rh.common.GatewayRequests.get;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.service.creator.LeaveCreator;
import com.hackaton.rh.leave.service.decision.LeaveRejecter;
import com.hackaton.rh.leave.service.decision.LeaveValidator;
import com.hackaton.rh.leave.service.reader.LeaveReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LeaveController.class)
class LeaveControllerReadTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private LeaveReader leaveReader;
    @MockitoBean
    private LeaveCreator leaveCreator;
    @MockitoBean
    private LeaveValidator leaveValidator;
    @MockitoBean
    private LeaveRejecter leaveRejecter;

    @Test
    void shouldReturn200WithFilteredLeaves_whenSearchingByEmployeeAndStatus() throws Exception {
        when(leaveReader.search(7L, LeaveStatus.DRAFT)).thenReturn(List.of(LeaveFixtures.leave(LeaveStatus.DRAFT)));

        mockMvc.perform(get("/leaves").param("employeeId", "7").param("status", "DRAFT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenStatusIsUnknown() throws Exception {
        mockMvc.perform(get("/leaves").param("status", "PENDING"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(leaveReader);
    }

    @Test
    void shouldReturn200WithLeave_whenLeaveExists() throws Exception {
        when(leaveReader.findById(1L)).thenReturn(LeaveFixtures.leave(LeaveStatus.VALIDATED));

        mockMvc.perform(get("/leaves/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.days").value(5))
                .andExpect(jsonPath("$.startDate").value("2026-12-01"));
    }

    @Test
    void shouldReturn404_whenLeaveDoesNotExist() throws Exception {
        when(leaveReader.findById(9L)).thenThrow(new ResourceNotFoundException("Congé", 9L));

        mockMvc.perform(get("/leaves/{id}", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
