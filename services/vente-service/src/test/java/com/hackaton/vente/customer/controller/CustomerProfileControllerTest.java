package com.hackaton.vente.customer.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.customer.controller.CustomerFixtures.customer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.dto.CustomerProfileResponse;
import com.hackaton.vente.customer.service.profile.CustomerProfileReader;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerProfileController.class)
class CustomerProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerProfileReader profileReader;

    @Test
    void shouldReturnProfile_whenCustomerExists() throws Exception {
        CustomerProfileResponse profile = new CustomerProfileResponse(customer(1L, "Rakoto"), 2,
                new BigDecimal("30000"), new BigDecimal("15000"), null, List.of(), List.of());
        when(profileReader.findProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/customers/1/profile").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customer.name").value("Rakoto"))
                .andExpect(jsonPath("$.salesCount").value(2))
                .andExpect(jsonPath("$.totalPurchased").value(30000));
    }

    @Test
    void shouldReturn404_whenProfileCustomerDoesNotExist() throws Exception {
        when(profileReader.findProfile(9L)).thenThrow(new ResourceNotFoundException("Client", 9L));

        mockMvc.perform(get("/customers/9/profile").with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
