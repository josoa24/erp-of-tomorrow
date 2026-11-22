package com.hackaton.stock.supplier.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.service.creator.SupplierCreator;
import com.hackaton.stock.supplier.service.deleter.SupplierDeleter;
import com.hackaton.stock.supplier.service.reader.SupplierReader;
import com.hackaton.stock.supplier.service.updater.SupplierUpdater;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(SupplierController.class)
class SupplierControllerUpdateTest {

    private static final String VALID_BODY = """
            {"name":"Agri Nord","usualDeliveryDays":7}""";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierReader supplierReader;
    @MockitoBean
    private SupplierCreator supplierCreator;
    @MockitoBean
    private SupplierUpdater supplierUpdater;
    @MockitoBean
    private SupplierDeleter supplierDeleter;

    private MockHttpServletRequestBuilder updateRequest(long id, String body) {
        return put("/suppliers/" + id).headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturnUpdatedSupplier_whenUpdateIsValid() throws Exception {
        when(supplierUpdater.update(eq(1L), any(SupplierRequest.class))).thenReturn(new SupplierResponse(
                1L, "Agri Nord", null, null, null, null, 7, Instant.now(), null));

        mockMvc.perform(updateRequest(1L, VALID_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Agri Nord"))
                .andExpect(jsonPath("$.usualDeliveryDays").value(7));
    }

    @Test
    void shouldReturn400_whenDeliveryDaysIsNegative() throws Exception {
        mockMvc.perform(updateRequest(1L, """
                        {"name":"Agri Nord","usualDeliveryDays":-1}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenUpdatingUnknownSupplier() throws Exception {
        when(supplierUpdater.update(eq(9L), any(SupplierRequest.class)))
                .thenThrow(new ResourceNotFoundException("Fournisseur", 9L));

        mockMvc.perform(updateRequest(9L, VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenNewNameIsTaken() throws Exception {
        when(supplierUpdater.update(eq(1L), any(SupplierRequest.class)))
                .thenThrow(new DuplicateResourceException("Le fournisseur Agri Nord existe déjà"));

        mockMvc.perform(updateRequest(1L, VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
