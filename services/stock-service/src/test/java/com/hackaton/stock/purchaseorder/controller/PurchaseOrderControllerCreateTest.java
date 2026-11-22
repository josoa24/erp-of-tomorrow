package com.hackaton.stock.purchaseorder.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static com.hackaton.stock.purchaseorder.controller.PurchaseOrderFixtures.order;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderRequest;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.service.creator.PurchaseOrderCreator;
import com.hackaton.stock.purchaseorder.service.reader.PurchaseOrderReader;
import com.hackaton.stock.purchaseorder.service.receiver.PurchaseOrderReceiver;
import com.hackaton.stock.purchaseorder.service.validator.PurchaseOrderValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest(PurchaseOrderController.class)
class PurchaseOrderControllerCreateTest {

    private static final String VALID_BODY = """
            {"supplierId":2,"lines":[{"productId":3,"quantity":10,"unitPrice":2.5}]}""";

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

    private MockHttpServletRequestBuilder createRequest(String body) {
        return post("/purchase-orders").headers(admin()).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    @Test
    void shouldReturn201_whenOrderIsCreatedAsDraft() throws Exception {
        when(orderCreator.create(any(PurchaseOrderRequest.class))).thenReturn(order(OrderStatus.DRAFT));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.totalAmount").value(25.00));
    }

    @Test
    void shouldReturn400_whenLinesAreEmpty() throws Exception {
        mockMvc.perform(createRequest("""
                        {"supplierId":2,"lines":[]}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn400_whenLineIsInvalid() throws Exception {
        mockMvc.perform(createRequest("""
                        {"supplierId":2,"lines":[{"quantity":0,"unitPrice":2.5}]}"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    @Test
    void shouldReturn404_whenSupplierDoesNotExist() throws Exception {
        when(orderCreator.create(any(PurchaseOrderRequest.class)))
                .thenThrow(new ResourceNotFoundException("Fournisseur", 2L));

        mockMvc.perform(createRequest(VALID_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
