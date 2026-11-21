package com.hackaton.vente.sale.controller;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.SaleLineResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import java.math.BigDecimal;
import java.util.List;

final class SaleResponseFixtures {

    static final String VALID_BODY = "{\"customerId\":1,\"lines\":[{\"productId\":10,\"quantity\":2}]}";

    private SaleResponseFixtures() {
    }

    static SaleResponse sale(Long id, DocumentStatus status) {
        SaleLineResponse line = new SaleLineResponse(10L, "Riz blanc", 2, new BigDecimal("2500"), new BigDecimal("5000"));
        return new SaleResponse(id, 1L, status, new BigDecimal("5000"), List.of(line), null, null);
    }
}
