package com.hackaton.vente.sale.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.VALID_BODY;
import static com.hackaton.vente.sale.controller.SaleResponseFixtures.sale;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ServiceUnavailableException;
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
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SaleController.class)
class SaleControllerCreateTest {

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

    private ResultActions postSale(String body) throws Exception {
        return mockMvc.perform(post("/sales").contentType(MediaType.APPLICATION_JSON).content(body).with(asAdmin()));
    }

    @Test
    void shouldReturn201WithDraft_whenSaleIsCreated() throws Exception {
        when(saleCreator.create(any(SaleRequest.class))).thenReturn(sale(5L, DocumentStatus.DRAFT));

        postSale(VALID_BODY)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenLinesAreEmpty() throws Exception {
        postSale("{\"customerId\":1,\"lines\":[]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenLineQuantityIsNotPositive() throws Exception {
        postSale("{\"lines\":[{\"productId\":10,\"quantity\":0}]}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn422_whenStockRefusesTheProduct() throws Exception {
        when(saleCreator.create(any(SaleRequest.class))).thenThrow(new BusinessException("Produit refuse"));

        postSale(VALID_BODY)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn503_whenStockIsUnavailable() throws Exception {
        when(saleCreator.create(any(SaleRequest.class)))
                .thenThrow(new ServiceUnavailableException("Le service Stock est indisponible"));

        postSale(VALID_BODY)
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("SERVER"));
    }
}
