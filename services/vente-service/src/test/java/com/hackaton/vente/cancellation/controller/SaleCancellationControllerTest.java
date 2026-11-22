package com.hackaton.vente.cancellation.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.cancellation.dto.CancellationRequest;
import com.hackaton.vente.cancellation.service.SaleCanceller;
import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.controller.SaleResponseFixtures;
import com.hackaton.vente.sale.dto.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SaleCancellationController.class)
class SaleCancellationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private SaleCanceller saleCanceller;

    private ResultActions cancel(String body) throws Exception {
        return mockMvc.perform(post("/sales/7/cancel")
                .contentType(MediaType.APPLICATION_JSON).content(body).with(asAdmin()));
    }

    @Test
    void shouldReturnCancelledSale_whenCancellationSucceeds() throws Exception {
        when(saleCanceller.cancel(eq(7L), any(CancellationRequest.class), eq(1L)))
                .thenReturn(SaleResponseFixtures.sale(7L, DocumentStatus.VALIDATED, PaymentStatus.CANCELLED));

        cancel("{\"reason\":\"Erreur de saisie\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus").value("CANCELLED"));
    }

    @Test
    void shouldReturn400_whenReasonIsBlank() throws Exception {
        cancel("{\"reason\":\" \"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn422_whenSaleAlreadyHasPayment() throws Exception {
        when(saleCanceller.cancel(eq(7L), any(CancellationRequest.class), eq(1L)))
                .thenThrow(new BusinessException("La vente V-0007 a déjà un encaissement"));

        cancel("{\"reason\":\"Retour client\"}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
