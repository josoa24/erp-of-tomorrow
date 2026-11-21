package com.hackaton.vente.customer.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.customer.controller.CustomerFixtures.customer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.customer.service.creator.CustomerCreator;
import com.hackaton.vente.customer.service.deleter.CustomerDeleter;
import com.hackaton.vente.customer.service.reader.CustomerReader;
import com.hackaton.vente.customer.service.updater.CustomerUpdater;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CustomerController.class)
class CustomerControllerReadTest {

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
    void shouldReturnPageAndTotalCountHeader_whenSearchingCustomers() throws Exception {
        when(customerReader.search("rak", 0, 1)).thenReturn(new PageResult<>(List.of(customer(1L, "Rakoto")), 3));

        mockMvc.perform(get("/customers").param("search", "rak").param("page", "0").param("size", "1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "3"))
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Rakoto"));
    }

    @Test
    void shouldReturn400_whenPageIsNotANumber() throws Exception {
        mockMvc.perform(get("/customers").param("page", "abc").with(asAdmin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturnCustomer_whenCustomerExists() throws Exception {
        when(customerReader.findById(1L)).thenReturn(customer(1L, "Rakoto"));

        mockMvc.perform(get("/customers/1").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.type").value("ENTREPRISE"));
    }

    @Test
    void shouldReturn404_whenCustomerDoesNotExist() throws Exception {
        when(customerReader.findById(9L)).thenThrow(new ResourceNotFoundException("Client", 9L));

        mockMvc.perform(get("/customers/9").with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
