package com.hackaton.rh.advance.controller;

import static com.hackaton.rh.common.GatewayRequests.postJson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.advance.dto.AdvanceCreateRequest;
import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.service.creator.AdvanceCreator;
import com.hackaton.rh.advance.service.reader.AdvanceReader;
import com.hackaton.rh.advance.service.summary.AdvanceSummaryReader;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AdvanceController.class)
class AdvanceControllerCreateTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdvanceReader advanceReader;
    @MockitoBean
    private AdvanceSummaryReader summaryReader;
    @MockitoBean
    private AdvanceCreator advanceCreator;

    @Test
    void shouldReturn201WithDraftAdvance_whenAmountIsWithinCap() throws Exception {
        when(advanceCreator.create(any(AdvanceCreateRequest.class)))
                .thenReturn(AdvanceFixtures.advance(AdvanceStatus.DRAFT));

        mockMvc.perform(postJson("/advances", AdvanceFixtures.VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenAmountIsNotPositive() throws Exception {
        mockMvc.perform(postJson("/advances", AdvanceFixtures.VALID_BODY.replace("80000", "0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(advanceCreator);
    }

    @Test
    void shouldReturn404_whenEmployeeDoesNotExist() throws Exception {
        when(advanceCreator.create(any(AdvanceCreateRequest.class)))
                .thenThrow(new ResourceNotFoundException("Employé", 7L));

        mockMvc.perform(postJson("/advances", AdvanceFixtures.VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422WithMessage_whenCapIsExceeded() throws Exception {
        when(advanceCreator.create(any(AdvanceCreateRequest.class)))
                .thenThrow(new BusinessException("Plafond d'avance dépassé"));

        mockMvc.perform(postJson("/advances", AdvanceFixtures.VALID_BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"))
                .andExpect(jsonPath("$.message").value("Plafond d'avance dépassé"));
    }
}
