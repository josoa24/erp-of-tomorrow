package com.hackaton.stock.invoice.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.service.creator.SupplierInvoiceCreator;
import com.hackaton.stock.invoice.service.reader.SupplierInvoiceReader;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceShield;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierInvoiceController.class)
class SupplierInvoiceControllerTest {

    static final String BODY = "{\"supplierId\":1,\"invoiceNumber\":\"FA-1\",\"invoiceDate\":\"2026-11-20\","
            + "\"lines\":[{\"productId\":1,\"description\":\"Riz\",\"quantity\":10,\"unitPrice\":120000}],"
            + "\"totalAmount\":1200000}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InvoiceShield invoiceShield;
    @MockitoBean
    private SupplierInvoiceCreator invoiceCreator;
    @MockitoBean
    private SupplierInvoiceReader invoiceReader;

    @Test
    void shouldReturnShieldReport_whenCheckingInvoice() throws Exception {
        when(invoiceShield.inspect(any())).thenReturn(ShieldReport.of(List.of(
                ShieldAlert.blocking(AlertCode.PROMPT_INJECTION, "consigne"))));

        mockMvc.perform(post("/supplier-invoices/check").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verdict").value("BLOCKED"))
                .andExpect(jsonPath("$.alerts[0].code").value("PROMPT_INJECTION"));
        verifyNoInteractions(invoiceCreator);
    }

    @Test
    void shouldReturn400_whenInvoiceHasNoLine() throws Exception {
        String body = BODY.replaceAll("\"lines\":\\[.*\\],", "\"lines\":[],");

        mockMvc.perform(post("/supplier-invoices").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
        verifyNoInteractions(invoiceCreator, invoiceShield);
    }

    @Test
    void shouldCreateWithCallerIdentity() throws Exception {
        when(invoiceCreator.create(any(), eq(new Actor(1L, "admin@erp.local", "ADMIN")))).thenReturn(null);

        mockMvc.perform(post("/supplier-invoices").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturn400_whenStatusFilterIsUnknown() throws Exception {
        mockMvc.perform(get("/supplier-invoices").param("status", "PAID").headers(admin()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404_whenInvoiceDoesNotExist() throws Exception {
        when(invoiceReader.findById(9L)).thenThrow(new ResourceNotFoundException("Facture fournisseur", 9L));

        mockMvc.perform(get("/supplier-invoices/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
