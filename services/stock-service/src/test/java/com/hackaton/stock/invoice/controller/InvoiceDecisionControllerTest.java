package com.hackaton.stock.invoice.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ForbiddenException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.invoice.dto.InvoiceEventResponse;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.service.decision.InvoiceDecider;
import com.hackaton.stock.invoice.service.reader.SupplierInvoiceReader;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(InvoiceDecisionController.class)
class InvoiceDecisionControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private InvoiceDecider invoiceDecider;
    @MockitoBean
    private SupplierInvoiceReader invoiceReader;

    @Test
    void shouldApproveWithoutBody_whenInvoiceIsCleared() throws Exception {
        mockMvc.perform(post("/supplier-invoices/12/approve").headers(admin()))
                .andExpect(status().isOk());

        verify(invoiceDecider).approve(eq(12L), isNull(), any());
    }

    @Test
    void shouldReturn403Forbidden_whenOverrideIsNotAllowed() throws Exception {
        when(invoiceDecider.approve(eq(12L), eq("ok"), any())).thenThrow(new ForbiddenException("ADMIN requis"));

        mockMvc.perform(post("/supplier-invoices/12/approve").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"justification\":\"ok\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn400_whenRejectionHasNoReason() throws Exception {
        mockMvc.perform(post("/supplier-invoices/12/reject").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\" \"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(invoiceDecider);
    }

    @Test
    void shouldReturn409Immutable_whenInvoiceAlreadyDecided() throws Exception {
        when(invoiceDecider.reject(eq(12L), eq("doublon"), any())).thenThrow(new ImmutableResourceException("déjà traitée"));

        mockMvc.perform(post("/supplier-invoices/12/reject").headers(admin())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"reason\":\"doublon\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturnAuditHistory() throws Exception {
        when(invoiceReader.history(12L)).thenReturn(List.of(new InvoiceEventResponse(
                InvoiceEventType.OVERRIDDEN, 1L, "admin@erp.local", "ADMIN", "RIB confirmé", Instant.now())));

        mockMvc.perform(get("/supplier-invoices/12/history").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("OVERRIDDEN"))
                .andExpect(jsonPath("$[0].actorEmail").value("admin@erp.local"));
    }
}
