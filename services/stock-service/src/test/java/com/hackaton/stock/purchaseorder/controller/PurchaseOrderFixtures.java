package com.hackaton.stock.purchaseorder.controller;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderLineResponse;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

final class PurchaseOrderFixtures {

    private PurchaseOrderFixtures() {
    }

    static PurchaseOrderResponse order(OrderStatus status) {
        PurchaseOrderLineResponse line = new PurchaseOrderLineResponse(
                3L, "Riz", 10, new BigDecimal("2.50"), new BigDecimal("25.00"));
        return new PurchaseOrderResponse(
                1L, 2L, status, new BigDecimal("25.00"), List.of(line), Instant.now(), null, null);
    }
}
