package com.hackaton.stock.supplier.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.service.creator.SupplierCreator;
import com.hackaton.stock.supplier.service.deleter.SupplierDeleter;
import com.hackaton.stock.supplier.service.reader.SupplierReader;
import com.hackaton.stock.supplier.service.updater.SupplierUpdater;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierController.class)
class SupplierControllerReadTest {

    private static final SupplierResponse AGRI = new SupplierResponse(
            1L, "Agri Sud", "0341234567", "contact@agrisud.mg", "Tana", "30 jours", 5, Instant.now(), null,
            "Antsirabe", "3000456789", "46101 11 2019 0 00456");

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

    @Test
    void shouldReturnSuppliers_whenSearching() throws Exception {
        when(supplierReader.search("agri")).thenReturn(List.of(AGRI));

        mockMvc.perform(get("/suppliers").param("search", "agri").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Agri Sud"));
    }

    @Test
    void shouldReturnSupplier_whenSupplierExists() throws Exception {
        when(supplierReader.findById(1L)).thenReturn(AGRI);

        mockMvc.perform(get("/suppliers/1").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.usualDeliveryDays").value(5))
                .andExpect(jsonPath("$.city").value("Antsirabe"))
                .andExpect(jsonPath("$.nif").value("3000456789"));
    }

    @Test
    void shouldReturn404_whenSupplierDoesNotExist() throws Exception {
        when(supplierReader.findById(9L)).thenThrow(new ResourceNotFoundException("Fournisseur", 9L));

        mockMvc.perform(get("/suppliers/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
