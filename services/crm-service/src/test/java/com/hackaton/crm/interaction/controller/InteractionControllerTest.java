package com.hackaton.crm.interaction.controller;

import static com.hackaton.crm.support.GatewayHeaders.withIdentity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.interaction.dto.InteractionResponse;
import com.hackaton.crm.interaction.entity.InteractionType;
import com.hackaton.crm.interaction.service.creator.InteractionCreator;
import com.hackaton.crm.interaction.service.reader.InteractionReader;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.support.GatewayHeaders;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InteractionController.class)
class InteractionControllerTest {

    private static final String URL = "/parties/CUSTOMER/7/interactions";
    private static final Instant DATE = Instant.parse("2026-11-20T10:00:00Z");

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InteractionCreator interactionCreator;
    @MockitoBean
    private InteractionReader interactionReader;

    @Test
    void shouldReturn200WithInteractions_whenPartyExists() throws Exception {
        when(interactionReader.findByParty(PartyType.CUSTOMER, 7L)).thenReturn(List.of(response()));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].type").value("APPEL"))
                .andExpect(jsonPath("$[0].summary").value("Relance facture"));
    }

    @Test
    void shouldReturn400_whenPartyTypeIsInvalid() throws Exception {
        mockMvc.perform(withIdentity(get("/parties/UNKNOWN/7/interactions")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(interactionReader);
    }

    @Test
    void shouldReturn404_whenPartyDoesNotExist() throws Exception {
        when(interactionReader.findByParty(PartyType.CUSTOMER, 7L))
                .thenThrow(new ResourceNotFoundException("Client", 7L));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn503_whenRemoteServiceIsUnavailable() throws Exception {
        when(interactionReader.findByParty(PartyType.CUSTOMER, 7L))
                .thenThrow(new ServiceUnavailableException("Service Vente indisponible"));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }

    @Test
    void shouldReturn201AndPassAuthorHeader_whenRequestIsValid() throws Exception {
        when(interactionCreator.create(eq(PartyType.CUSTOMER), eq(7L), any(), eq(GatewayHeaders.USER_EMAIL)))
                .thenReturn(response());

        mockMvc.perform(withIdentity(post(URL)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\":\"APPEL\",\"summary\":\"Relance facture\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value(GatewayHeaders.USER_EMAIL));
    }

    private static InteractionResponse response() {
        return new InteractionResponse(1L, PartyType.CUSTOMER, 7L, InteractionType.APPEL, DATE,
                "Relance facture", GatewayHeaders.USER_EMAIL, DATE);
    }
}
