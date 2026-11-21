package com.hackaton.rh.leave.controller;

import static com.hackaton.rh.common.GatewayRequests.postJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.leave.dto.LeaveCreateRequest;
import com.hackaton.rh.leave.entity.LeaveStatus;
import com.hackaton.rh.leave.service.creator.LeaveCreator;
import com.hackaton.rh.leave.service.decision.LeaveRejecter;
import com.hackaton.rh.leave.service.decision.LeaveValidator;
import com.hackaton.rh.leave.service.reader.LeaveReader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(LeaveController.class)
class LeaveControllerCreateTest {

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
    void shouldReturn201WithDraftLeave_whenRequestIsValid() throws Exception {
        when(leaveCreator.create(any(LeaveCreateRequest.class))).thenReturn(LeaveFixtures.leave(LeaveStatus.DRAFT));

        mockMvc.perform(postJson("/leaves", LeaveFixtures.VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenStartDateIsMissing() throws Exception {
        String body = "{\"employeeId\":7,\"endDate\":\"2026-12-05\"}";

        mockMvc.perform(postJson("/leaves", body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenEmployeeDoesNotExist() throws Exception {
        when(leaveCreator.create(any(LeaveCreateRequest.class))).thenThrow(new ResourceNotFoundException("Employé", 7L));

        mockMvc.perform(postJson("/leaves", LeaveFixtures.VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenBalanceIsInsufficient() throws Exception {
        when(leaveCreator.create(any(LeaveCreateRequest.class))).thenThrow(new BusinessException("Solde insuffisant"));

        mockMvc.perform(postJson("/leaves", LeaveFixtures.VALID_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
