package com.hackaton.stock.supplier.controller;

import static com.hackaton.stock.common.AuthHeaders.admin;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.supplier.dto.RecentOrder;
import com.hackaton.stock.supplier.dto.SuppliedProduct;
import com.hackaton.stock.supplier.dto.SupplierProfileResponse;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.service.profile.SupplierProfileReader;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SupplierProfileController.class)
class SupplierProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupplierProfileReader profileReader;

    @Test
    void shouldReturnProfile_whenSupplierExists() throws Exception {
        SupplierResponse supplier = new SupplierResponse(1L, "Agri Sud", null, null, null, null, 5, Instant.now());
        when(profileReader.findProfile(1L)).thenReturn(new SupplierProfileResponse(
                supplier, 3, 2, new BigDecimal("750.00"), new BigDecimal("4.5"),
                List.of(new SuppliedProduct(3L, "Riz", 120)),
                List.of(new RecentOrder(7L, OrderStatus.RECEIVED, new BigDecimal("250.00"), Instant.now()))));

        mockMvc.perform(get("/suppliers/1/profile").headers(admin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supplier.name").value("Agri Sud"))
                .andExpect(jsonPath("$.ordersCount").value(3))
                .andExpect(jsonPath("$.suppliedProducts[0].totalQuantity").value(120))
                .andExpect(jsonPath("$.recentOrders[0].status").value("RECEIVED"));
    }

    @Test
    void shouldReturn404_whenSupplierDoesNotExist() throws Exception {
        when(profileReader.findProfile(9L)).thenThrow(new ResourceNotFoundException("Fournisseur", 9L));

        mockMvc.perform(get("/suppliers/9/profile").headers(admin()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }
}
