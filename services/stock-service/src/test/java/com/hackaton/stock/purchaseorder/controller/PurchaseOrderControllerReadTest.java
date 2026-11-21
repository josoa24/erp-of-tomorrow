package com.hackaton.stock.purchaseorder.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static com.hackaton.stock.purchaseorder.controller.PurchaseOrderFixtures.order;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.service.creator.PurchaseOrderCreator;
import com.hackaton.stock.purchaseorder.service.reader.PurchaseOrderReader;
import com.hackaton.stock.purchaseorder.service.receiver.PurchaseOrderReceiver;
import com.hackaton.stock.purchaseorder.service.validator.PurchaseOrderValidator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PurchaseOrderController.class)
class PurchaseOrderControllerReadTest {

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
    void shouldReturnOrders_whenFilteringBySupplierAndStatus() throws Exception {
        when(orderReader.search(2L, OrderStatus.DRAFT)).thenReturn(List.of(order(OrderStatus.DRAFT)));

        mockMvc.perform(get("/purchase-orders").param("supplierId", "2").param("status", "DRAFT").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].supplierId").value(2))
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    @Test
    void shouldReturn400_whenStatusIsUnknown() throws Exception {
        mockMvc.perform(get("/purchase-orders").param("status", "CANCELLED").headers(admin()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturnOrderWithLines_whenOrderExists() throws Exception {
        when(orderReader.findById(1L)).thenReturn(order(OrderStatus.DRAFT));

        mockMvc.perform(get("/purchase-orders/1").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.lines[0].lineTotal").value(25.00));
    }

    @Test
    void shouldReturn404_whenOrderDoesNotExist() throws Exception {
        when(orderReader.findById(9L)).thenThrow(new ResourceNotFoundException("Bon de commande", 9L));

        mockMvc.perform(get("/purchase-orders/9").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
