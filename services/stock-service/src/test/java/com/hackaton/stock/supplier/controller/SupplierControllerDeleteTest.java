package com.hackaton.stock.supplier.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.supplier.service.creator.SupplierCreator;
import com.hackaton.stock.supplier.service.deleter.SupplierDeleter;
import com.hackaton.stock.supplier.service.reader.SupplierReader;
import com.hackaton.stock.supplier.service.updater.SupplierUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierController.class)
class SupplierControllerDeleteTest {

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
    void shouldReturn204_whenSupplierIsDeleted() throws Exception {
        mockMvc.perform(delete("/suppliers/1").headers(admin()))
                .andExpect(status().isNoContent());
        verify(supplierDeleter).delete(1L);
    }

    @Test
    void shouldReturn404_whenDeletingUnknownSupplier() throws Exception {
        doThrow(new ResourceNotFoundException("Fournisseur", 9L)).when(supplierDeleter).delete(9L);

        mockMvc.perform(delete("/suppliers/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn422_whenSupplierHasPurchaseOrders() throws Exception {
        doThrow(new BusinessException("Ce fournisseur a des bons de commande")).when(supplierDeleter).delete(1L);

        mockMvc.perform(delete("/suppliers/1").headers(admin()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
