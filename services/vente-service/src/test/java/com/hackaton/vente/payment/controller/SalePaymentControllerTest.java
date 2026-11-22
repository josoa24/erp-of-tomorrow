package com.hackaton.vente.payment.controller;

import static com.hackaton.vente.common.UserHeaders.asAdmin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.payment.dto.PaymentRequest;
import com.hackaton.vente.payment.dto.PaymentResponse;
import com.hackaton.vente.payment.service.reader.PaymentReader;
import com.hackaton.vente.payment.service.recorder.PaymentRecorder;
import com.hackaton.vente.sale.entity.PaymentMethod;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(SalePaymentController.class)
class SalePaymentControllerTest {

    private static final PaymentResponse PAYMENT = new PaymentResponse(
            1L, 411L, new BigDecimal("420000"), PaymentMethod.MVOLA, Instant.parse("2026-11-24T07:00:00Z"), null, 1L);

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PaymentRecorder paymentRecorder;
    @MockitoBean
    private PaymentReader paymentReader;

    private ResultActions postPayment(String body) throws Exception {
        return mockMvc.perform(post("/sales/411/payments")
                .contentType(MediaType.APPLICATION_JSON).content(body).with(asAdmin()));
    }

    @Test
    void shouldReturn201WithAuthor_whenPaymentIsRecorded() throws Exception {
        when(paymentRecorder.record(eq(411L), any(PaymentRequest.class), eq(1L))).thenReturn(PAYMENT);

        postPayment("{\"amount\":420000,\"method\":\"MVOLA\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.saleId").value(411))
                .andExpect(jsonPath("$.method").value("MVOLA"));
    }

    @Test
    void shouldReturn400_whenAmountIsNotPositive() throws Exception {
        postPayment("{\"amount\":0,\"method\":\"CASH\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenMethodIsMissing() throws Exception {
        postPayment("{\"amount\":1000}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn422_whenRuleRefusesPayment() throws Exception {
        when(paymentRecorder.record(eq(411L), any(PaymentRequest.class), eq(1L)))
                .thenThrow(new BusinessException("Le montant dépasse le reste à encaisser"));

        postPayment("{\"amount\":999999,\"method\":\"CASH\"}")
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldListPayments_whenSaleExists() throws Exception {
        when(paymentReader.findBySale(411L)).thenReturn(List.of(PAYMENT));

        mockMvc.perform(get("/sales/411/payments").with(asAdmin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(420000));
    }
}
