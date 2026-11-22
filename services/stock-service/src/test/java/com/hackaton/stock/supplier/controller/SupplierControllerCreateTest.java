package com.hackaton.stock.supplier.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.DuplicateResourceException;
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
class SupplierControllerCreateTest {

    private static final String VALID_BODY = """
            {"name":"Agri Sud","email":"contact@agrisud.mg","usualDeliveryDays":5}""";

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

    private MockHttpServletRequestBuilder createRequest(String body) {
        return post("/suppliers").headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturn201_whenSupplierIsCreated() throws Exception {
        when(supplierCreator.create(any(SupplierRequest.class))).thenReturn(new SupplierResponse(
                1L, "Agri Sud", null, "contact@agrisud.mg", null, null, 5, Instant.now(), null, "Toamasina", null, null));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Agri Sud"))
                .andExpect(jsonPath("$.city").value("Toamasina"));
    }

    @Test
    void shouldReturn400_whenCityIsTooLong() throws Exception {
        mockMvc.perform(createRequest("{\"name\":\"Agri Sud\",\"city\":\"" + "a".repeat(81) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenStatIsTooLong() throws Exception {
        mockMvc.perform(createRequest("{\"name\":\"Agri Sud\",\"stat\":\"" + "1".repeat(31) + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenNameIsBlank() throws Exception {
        mockMvc.perform(createRequest("""
                        {"name":" "}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenEmailIsInvalid() throws Exception {
        mockMvc.perform(createRequest("""
                        {"name":"Agri Sud","email":"pas-un-email"}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn409_whenSupplierNameAlreadyExists() throws Exception {
        when(supplierCreator.create(any(SupplierRequest.class)))
                .thenThrow(new DuplicateResourceException("Le fournisseur Agri Sud existe déjà"));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
