package com.hackaton.crm.contact.controller;

import static com.hackaton.crm.contact.ContactFixtures.contact;
import static com.hackaton.crm.support.GatewayHeaders.withIdentity;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.contact.dto.ContactCountsResponse;
import com.hackaton.crm.contact.dto.ContactFilter;
import com.hackaton.crm.contact.dto.ContactKind;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.contact.dto.ContactSummaryResponse;
import com.hackaton.crm.party.PartyType;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContactControllerReadTest extends ContactControllerSlice {

    @Test
    void shouldListEveryContact_whenNoFilterIsGiven() throws Exception {
        when(contactReader.search(new ContactFilter(ContactKind.ALL, null, false, false))).thenReturn(List.of(
                contact(PartyType.CUSTOMER, "Hery Rabe", "2026-11-20T07:00:00Z", ContactStatus.ACTIVE)));

        mockMvc.perform(withIdentity(get("/contacts")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kind").value("CUSTOMER"))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].lastInteractionAt").value("2026-11-20T07:00:00Z"));
    }

    @Test
    void shouldPassEveryFilter_whenTheyAreGiven() throws Exception {
        when(contactReader.search(new ContactFilter(ContactKind.CUSTOMER, "soa", true, true))).thenReturn(List.of());

        mockMvc.perform(withIdentity(get("/contacts").param("kind", "CUSTOMER").param("search", "soa")
                        .param("followUp", "true").param("professional", "true")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturn400_whenKindIsUnknown() throws Exception {
        mockMvc.perform(withIdentity(get("/contacts").param("kind", "PARTENAIRE")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(contactReader);
    }

    @Test
    void shouldReturnCounts_whenAskedWithASearch() throws Exception {
        when(contactReader.count("rabe")).thenReturn(new ContactCountsResponse(1, 1, 0, 0, 0));

        mockMvc.perform(withIdentity(get("/contacts/counts").param("search", "rabe")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.all").value(1))
                .andExpect(jsonPath("$.customers").value(1));
    }

    @Test
    void shouldReturnSummary_whenServicesAnswer() throws Exception {
        when(contactSummaryReader.summarize()).thenReturn(
                new ContactSummaryResponse(38, 52, 14, 3, 6, new BigDecimal("4200000"), 2));

        mockMvc.perform(withIdentity(get("/contacts/summary")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeCustomers").value(38))
                .andExpect(jsonPath("$.creditOutstanding").value(4200000))
                .andExpect(jsonPath("$.overLimitCustomers").value(2));
    }

    @Test
    void shouldReturn503Server_whenVenteIsDown() throws Exception {
        when(contactSummaryReader.summarize()).thenThrow(new ServiceUnavailableException("Le service Vente est indisponible"));

        mockMvc.perform(withIdentity(get("/contacts/summary")))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
