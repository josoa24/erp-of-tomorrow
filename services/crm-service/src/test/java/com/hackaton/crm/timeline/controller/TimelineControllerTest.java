package com.hackaton.crm.timeline.controller;

import static com.hackaton.crm.support.GatewayHeaders.withIdentity;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.crm.common.exception.ResourceNotFoundException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.party.PartyType;
import com.hackaton.crm.timeline.dto.TimelineEntry;
import com.hackaton.crm.timeline.dto.TimelineKind;
import com.hackaton.crm.timeline.service.TimelineReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(TimelineController.class)
class TimelineControllerTest {

    private static final String URL = "/parties/SUPPLIER/3/timeline";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TimelineReader timelineReader;

    @Test
    void shouldReturn200WithEntries_whenPartyExists() throws Exception {
        TimelineEntry entry = new TimelineEntry(TimelineKind.PURCHASE_ORDER, Instant.parse("2026-11-20T10:00:00Z"),
                "Bon de commande BC-12", "VALIDATED", new BigDecimal("150.00"), 12L);
        when(timelineReader.findTimeline(PartyType.SUPPLIER, 3L)).thenReturn(List.of(entry));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kind").value("PURCHASE_ORDER"))
                .andExpect(jsonPath("$[0].amount").value(150.00))
                .andExpect(jsonPath("$[0].reference").value(12));
    }

    @Test
    void shouldReturn400_whenPartyTypeIsInvalid() throws Exception {
        mockMvc.perform(withIdentity(get("/parties/PARTNER/3/timeline")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(timelineReader);
    }

    @Test
    void shouldReturn404_whenPartyDoesNotExist() throws Exception {
        when(timelineReader.findTimeline(PartyType.SUPPLIER, 3L))
                .thenThrow(new ResourceNotFoundException("Fournisseur", 3L));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn503_whenRemoteServiceIsUnavailable() throws Exception {
        when(timelineReader.findTimeline(PartyType.SUPPLIER, 3L))
                .thenThrow(new ServiceUnavailableException("Service Stock indisponible"));

        mockMvc.perform(withIdentity(get(URL)))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
