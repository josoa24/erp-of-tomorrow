package com.hackaton.vente.customer.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.customer.controller.CustomerFixtures.VALID_BODY;
import static com.hackaton.vente.customer.controller.CustomerFixtures.customer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.dto.CustomerRequest;
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

@WebMvcTest(CustomerController.class)
class CustomerControllerWriteTest {

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

    @Test
    void shouldReturn201_whenCustomerIsCreated() throws Exception {
        when(customerCreator.create(any(CustomerRequest.class))).thenReturn(customer(1L, "Epicerie Rakoto"));

        mockMvc.perform(post("/customers").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY).with(asAdmin()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Epicerie Rakoto"));
    }

    @Test
    void shouldReturnUpdatedCustomer_whenCustomerIsUpdated() throws Exception {
        when(customerUpdater.update(eq(1L), any(CustomerRequest.class))).thenReturn(customer(1L, "Epicerie Rakoto"));

        mockMvc.perform(put("/customers/1").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Epicerie Rakoto"));
    }

    @Test
    void shouldReturn404_whenUpdatedCustomerDoesNotExist() throws Exception {
        when(customerUpdater.update(eq(9L), any(CustomerRequest.class)))
                .thenThrow(new ResourceNotFoundException("Client", 9L));

        mockMvc.perform(put("/customers/9").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY).with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn204_whenCustomerIsDeleted() throws Exception {
        mockMvc.perform(delete("/customers/1").with(asAdmin()))
                .andExpect(status().isNoContent());

        verify(customerDeleter).delete(1L);
    }

    @Test
    void shouldReturn422_whenCustomerHasSales() throws Exception {
        doThrow(new BusinessException("Client utilise")).when(customerDeleter).delete(1L);

        mockMvc.perform(delete("/customers/1").with(asAdmin()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
