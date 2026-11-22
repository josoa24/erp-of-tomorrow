package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.VALID_BODY;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.sale;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.ImmutableResourceException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.service.creator.SaleCreator;
import com.hackaton.vente.sale.service.deleter.SaleDeleter;
import com.hackaton.vente.sale.service.reader.SaleReader;
import com.hackaton.vente.sale.service.updater.SaleUpdater;
import com.hackaton.vente.sale.service.validator.SaleValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SaleController.class)
class SaleControllerUpdateTest {

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
    void shouldReturnUpdatedSale_whenDraftIsUpdated() throws Exception {
        when(saleUpdater.update(eq(5L), any(SaleRequest.class))).thenReturn(sale(5L, DocumentStatus.DRAFT));

        mockMvc.perform(put("/sales/5").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY).with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lines[0].quantity").value(2));
    }

    @Test
    void shouldReturn409_whenUpdatingValidatedSale() throws Exception {
        when(saleUpdater.update(eq(5L), any(SaleRequest.class)))
                .thenThrow(new ImmutableResourceException("Vente validee"));

        mockMvc.perform(put("/sales/5").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY).with(asAdmin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturn204_whenDraftIsDeleted() throws Exception {
        mockMvc.perform(delete("/sales/5").with(asAdmin()))
                .andExpect(status().isNoContent());

        verify(saleDeleter).delete(5L);
    }

    @Test
    void shouldReturn409_whenDeletingValidatedSale() throws Exception {
        doThrow(new ImmutableResourceException("Vente validee")).when(saleDeleter).delete(5L);

        mockMvc.perform(delete("/sales/5").with(asAdmin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }
}
