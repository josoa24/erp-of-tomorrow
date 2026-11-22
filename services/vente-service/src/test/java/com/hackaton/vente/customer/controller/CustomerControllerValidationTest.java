package com.hackaton.vente.customer.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.customer.service.creator.CustomerCreator;
import com.hackaton.vente.customer.service.deleter.CustomerDeleter;
import com.hackaton.vente.customer.service.reader.CustomerReader;
import com.hackaton.vente.customer.service.updater.CustomerUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CustomerController.class)
class CustomerControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerReader customerReader;

    @MockitoBean
    private CustomerCreator customerCreator;

    @MockitoBean
    private CustomerUpdater customerUpdater;

    @MockitoBean
    private CustomerDeleter customerDeleter;

    private ResultActions postCustomer(String body) throws Exception {
        return mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(body).with(asAdmin()));
    }

    @Test
    void shouldReturn400_whenNameIsBlank() throws Exception {
        postCustomer("{\"name\":\" \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenEmailIsMalformed() throws Exception {
        postCustomer("{\"name\":\"Soa\",\"email\":\"pas-un-email\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenTypeIsUnknown() throws Exception {
        postCustomer("{\"name\":\"Soa\",\"type\":\"INCONNU\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenNifIsTooLong() throws Exception {
        postCustomer("{\"name\":\"Soa\",\"nif\":\"123456789012345678901\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(startsWith("nif :")));
    }

    @Test
    void shouldReturn400_whenCreditLimitIsNegative() throws Exception {
        postCustomer("{\"name\":\"Soa\",\"creditLimit\":-1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenCreditLimitHasMoreThanTwoDecimals() throws Exception {
        postCustomer("{\"name\":\"Soa\",\"creditLimit\":1000.555}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenBodyIsMalformed() throws Exception {
        postCustomer("{pas du json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }
}
