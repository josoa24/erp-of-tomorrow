package com.hackaton.stock.purchaseorder.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static com.hackaton.stock.purchaseorder.controller.PurchaseOrderFixtures.order;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.BusinessException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.service.creator.PurchaseOrderCreator;
import com.hackaton.stock.purchaseorder.service.reader.PurchaseOrderReader;
import com.hackaton.stock.purchaseorder.service.receiver.PurchaseOrderReceiver;
import com.hackaton.stock.purchaseorder.service.validator.PurchaseOrderValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PurchaseOrderController.class)
class PurchaseOrderControllerWorkflowTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseOrderReader orderReader;
    @MockitoBean
    private PurchaseOrderCreator orderCreator;
    @MockitoBean
    private PurchaseOrderValidator orderValidator;
    @MockitoBean
    private PurchaseOrderReceiver orderReceiver;

    @Test
    void shouldReturnValidatedOrder_whenOrderIsDraft() throws Exception {
        when(orderValidator.validate(1L)).thenReturn(order(OrderStatus.VALIDATED));

        mockMvc.perform(post("/purchase-orders/1/validate").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("VALIDATED"));
    }

    @Test
    void shouldReturn404_whenValidatingUnknownOrder() throws Exception {
        when(orderValidator.validate(9L)).thenThrow(new ResourceNotFoundException("Bon de commande", 9L));

        mockMvc.perform(post("/purchase-orders/9/validate").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn409_whenOrderIsAlreadyValidated() throws Exception {
        when(orderValidator.validate(1L)).thenThrow(new ImmutableResourceException("Déjà validé"));

        mockMvc.perform(post("/purchase-orders/1/validate").headers(admin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturnReceivedOrder_whenOrderIsValidated() throws Exception {
        when(orderReceiver.receive(1L)).thenReturn(order(OrderStatus.RECEIVED));

        mockMvc.perform(post("/purchase-orders/1/receive").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void shouldReturn409_whenOrderIsAlreadyReceived() throws Exception {
        when(orderReceiver.receive(1L)).thenThrow(new ImmutableResourceException("Déjà réceptionné"));

        mockMvc.perform(post("/purchase-orders/1/receive").headers(admin()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("IMMUTABLE"));
    }

    @Test
    void shouldReturn422_whenReceivingDraftOrder() throws Exception {
        when(orderReceiver.receive(1L)).thenThrow(new BusinessException("Le bon doit être validé"));

        mockMvc.perform(post("/purchase-orders/1/receive").headers(admin()))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
