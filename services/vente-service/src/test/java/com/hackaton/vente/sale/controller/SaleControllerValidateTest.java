package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.sale;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.service.creator.SaleCreator;
import com.hackaton.vente.sale.service.deleter.SaleDeleter;
import com.hackaton.vente.sale.service.reader.SaleReader;
import com.hackaton.vente.sale.service.updater.SaleUpdater;
import com.hackaton.vente.sale.service.validator.SaleValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerValidateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaleReader saleReader;

    @MockitoBean
    private SaleCreator saleCreator;

    @MockitoBean
    private SaleUpdater saleUpdater;

    @MockitoBean
    private SaleDeleter saleDeleter;

    @MockitoBean
    private SaleValidator saleValidator;

    @Test
    void shouldReturnValidatedSale_whenValidationSucceeds() throws Exception {
        when(saleValidator.validate(5L)).thenReturn(sale(5L, DocumentStatus.VALIDATED));

        mockMvc.perform(post("/sales/5/validate").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    @Test
    void shouldReturn404_whenValidatedSaleDoesNotExist() throws Exception {
        when(saleValidator.validate(9L)).thenThrow(new ResourceNotFoundException("Vente", 9L));

        mockMvc.perform(post("/sales/9/validate").with(asAdmin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenSaleIsAlreadyValidated() throws Exception {
        when(saleValidator.validate(5L)).thenThrow(new ImmutableResourceException("Vente deja validee"));

        mockMvc.perform(post("/sales/5/validate").with(asAdmin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturn422_whenStockIsInsufficient() throws Exception {
        when(saleValidator.validate(5L)).thenThrow(new BusinessException("Stock insuffisant"));

        mockMvc.perform(post("/sales/5/validate").with(asAdmin()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn503_whenStockIsUnavailableDuringValidation() throws Exception {
        when(saleValidator.validate(5L)).thenThrow(new ServiceUnavailableException("Le service Stock est indisponible"));

        mockMvc.perform(post("/sales/5/validate").with(asAdmin()))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
