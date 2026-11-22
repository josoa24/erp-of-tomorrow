package com.hackaton.crm.contact.controller;

import static com.hackaton.crm.contact.ContactFixtures.contact;
import static com.hackaton.crm.support.GatewayHeaders.withIdentity;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.crm.common.exception.BusinessException;
import com.hackaton.crm.common.exception.ServiceUnavailableException;
import com.hackaton.crm.contact.dto.ContactStatus;
import com.hackaton.crm.party.PartyType;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

class ContactControllerCreateTest extends ContactControllerSlice {

    private static final String CUSTOMER = """
            {"kind":"CUSTOMER","name":"Hotely Soa","city":"Toamasina","type":"ENTREPRISE","nif":"4000123456",\
            "creditLimit":2000000}""";

    private ResultActions postContact(String body) throws Exception {
        return mockMvc.perform(withIdentity(post("/contacts").contentType(MediaType.APPLICATION_JSON).content(body)));
    }

    private void expectValidationError(String body, String field) throws Exception {
        postContact(body)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"))
                .andExpect(jsonPath("$.message").value(startsWith(field + " :")));
        verifyNoInteractions(contactCreator);
    }

    @Test
    void shouldReturn201WithTheContact_whenCustomerIsCreated() throws Exception {
        when(contactCreator.create(any()))
                .thenReturn(contact(PartyType.CUSTOMER, "Hotely Soa", null, ContactStatus.ACTIVE));

        postContact(CUSTOMER)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Hotely Soa"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void shouldReturn400_whenKindIsMissing() throws Exception {
        expectValidationError("{\"name\":\"Hotely Soa\"}", "kind");
    }

    @Test
    void shouldReturn400_whenNameIsBlank() throws Exception {
        expectValidationError("{\"kind\":\"SUPPLIER\",\"name\":\" \"}", "name");
    }

    @Test
    void shouldReturn400_whenSupplierCarriesACreditLimit() throws Exception {
        expectValidationError("{\"kind\":\"SUPPLIER\",\"name\":\"Socolait\",\"creditLimit\":1000}",
                "supplierWithoutCustomerFields");
    }

    @Test
    void shouldReturn400_whenNifIsTooLong() throws Exception {
        expectValidationError("{\"kind\":\"CUSTOMER\",\"name\":\"Soa\",\"nif\":\"123456789012345678901\"}", "nif");
    }

    @Test
    void shouldReturn400_whenCreditLimitIsNegative() throws Exception {
        expectValidationError("{\"kind\":\"CUSTOMER\",\"name\":\"Soa\",\"creditLimit\":-5}", "creditLimit");
    }

    @Test
    void shouldReturn422Business_whenTheOwningServiceRefuses() throws Exception {
        when(contactCreator.create(any())).thenThrow(new BusinessException("Le fournisseur Socolait existe déjà"));

        postContact("{\"kind\":\"SUPPLIER\",\"name\":\"Socolait\"}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Le fournisseur Socolait existe déjà"));
    }

    @Test
    void shouldReturn503_whenTheOwningServiceIsDown() throws Exception {
        when(contactCreator.create(any())).thenThrow(new ServiceUnavailableException("Le service Vente est indisponible"));

        postContact(CUSTOMER)
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
