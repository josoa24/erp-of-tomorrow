package com.hackaton.rh.leave.controller;

import static com.hackaton.rh.common.GatewayRequests.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
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
class LeaveControllerValidateTest {

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
    void shouldReturn200WithValidatedLeave_whenLeaveIsDraft() throws Exception {
        when(leaveValidator.validate(1L)).thenReturn(LeaveFixtures.leave(LeaveStatus.VALIDATED));

        mockMvc.perform(post("/leaves/{id}/validate", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    @Test
    void shouldReturn404_whenValidatingUnknownLeave() throws Exception {
        when(leaveValidator.validate(9L)).thenThrow(new ResourceNotFoundException("Congé", 9L));

        mockMvc.perform(post("/leaves/{id}/validate", 9L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenValidatingAlreadyDecidedLeave() throws Exception {
        when(leaveValidator.validate(1L)).thenThrow(new ImmutableResourceException("déjà traité"));

        mockMvc.perform(post("/leaves/{id}/validate", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturn422_whenBalanceIsInsufficientAtValidation() throws Exception {
        when(leaveValidator.validate(1L)).thenThrow(new BusinessException("Solde insuffisant"));

        mockMvc.perform(post("/leaves/{id}/validate", 1L))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
