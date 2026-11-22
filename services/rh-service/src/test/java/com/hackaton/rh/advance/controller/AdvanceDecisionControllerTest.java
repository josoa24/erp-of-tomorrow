package com.hackaton.rh.advance.controller;

import static com.hackaton.rh.common.GatewayRequests.post;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.rh.advance.entity.AdvanceStatus;
import com.hackaton.rh.advance.service.decision.AdvanceRejecter;
import com.hackaton.rh.advance.service.decision.AdvanceValidator;
import com.hackaton.rh.common.exception.BusinessException;
import com.hackaton.rh.common.exception.ForbiddenException;
import com.hackaton.rh.common.exception.ImmutableResourceException;
import com.hackaton.rh.common.exception.ResourceNotFoundException;
import com.hackaton.rh.common.security.Actor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AdvanceDecisionController.class)
class AdvanceDecisionControllerTest {

    private static final Actor ADMIN = new Actor(1L, "ADMIN");

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private AdvanceValidator advanceValidator;
    @MockitoBean
    private AdvanceRejecter advanceRejecter;

    @Test
    void shouldReturn200WithValidatedAdvance_whenAdminValidates() throws Exception {
        when(advanceValidator.validate(3L, ADMIN)).thenReturn(AdvanceFixtures.advance(AdvanceStatus.VALIDATED));

        mockMvc.perform(post("/advances/{id}/validate", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"))
                .andExpect(jsonPath("$.decidedBy").value(1));
    }

    @Test
    void shouldReturn403_whenHumanResourcesValidates() throws Exception {
        when(advanceValidator.validate(3L, new Actor(2L, "RH"))).thenThrow(new ForbiddenException("gérant"));

        mockMvc.perform(MockMvcRequestBuilders.post("/advances/{id}/validate", 3L)
                        .header("X-User-Id", "2").header("X-User-Role", "RH"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn404_whenValidatingUnknownAdvance() throws Exception {
        when(advanceValidator.validate(9L, ADMIN)).thenThrow(new ResourceNotFoundException("Avance", 9L));

        mockMvc.perform(post("/advances/{id}/validate", 9L)).andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn422_whenCapIsExceededAtValidation() throws Exception {
        when(advanceValidator.validate(3L, ADMIN)).thenThrow(new BusinessException("Plafond d'avance dépassé"));

        mockMvc.perform(post("/advances/{id}/validate", 3L))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn200WithRejectedAdvance_whenAdminRejects() throws Exception {
        when(advanceRejecter.reject(3L, ADMIN)).thenReturn(AdvanceFixtures.advance(AdvanceStatus.REJECTED));

        mockMvc.perform(post("/advances/{id}/reject", 3L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldReturn409_whenRejectingDecidedAdvance() throws Exception {
        when(advanceRejecter.reject(3L, ADMIN)).thenThrow(new ImmutableResourceException("déjà traitée"));

        mockMvc.perform(post("/advances/{id}/reject", 3L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }
}
