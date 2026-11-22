package com.hackaton.crm.interaction.controller;

import static com.hackaton.crm.support.GatewayHeaders.withIdentity;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.crm.interaction.service.creator.InteractionCreator;
import com.hackaton.crm.interaction.service.reader.InteractionReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(InteractionController.class)
class InteractionControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InteractionCreator interactionCreator;
    @MockitoBean
    private InteractionReader interactionReader;

    @Test
    void shouldReturn400_whenTypeIsMissing() throws Exception {
        postInteraction("{\"summary\":\"Relance facture\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(interactionCreator);
    }

    @Test
    void shouldReturn400_whenSummaryIsTooLong() throws Exception {
        String summary = "a".repeat(2001);

        postInteraction("{\"type\":\"NOTE\",\"summary\":\"" + summary + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(interactionCreator);
    }

    @Test
    void shouldReturn400_whenOccurredAtIsInTheFuture() throws Exception {
        Instant future = Instant.now().plus(1, ChronoUnit.DAYS);

        postInteraction("{\"type\":\"NOTE\",\"summary\":\"Rappel\",\"occurredAt\":\"" + future + "\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(interactionCreator);
    }

    private ResultActions postInteraction(String body) throws Exception {
        return mockMvc.perform(withIdentity(post("/parties/CUSTOMER/7/interactions"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
