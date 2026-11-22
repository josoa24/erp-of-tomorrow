package com.hackaton.vente.sale.controller;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.dto.PaymentStatus;
import com.hackaton.vente.sale.dto.SaleLineResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.PaymentMethod;
import java.math.BigDecimal;
import java.util.List;

public final class SaleResponseFixtures {

    static final String VALID_BODY = "{\"customerId\":1,\"lines\":[{\"productId\":10,\"quantity\":2}]}";

    private SaleResponseFixtures() {
    }

    public static SaleResponse sale(Long id, DocumentStatus status) {
        PaymentStatus paymentStatus = status == DocumentStatus.DRAFT ? PaymentStatus.DRAFT : PaymentStatus.TO_COLLECT;
        return sale(id, status, paymentStatus);
    }

    public static SaleResponse sale(Long id, DocumentStatus status, PaymentStatus paymentStatus) {
        SaleLineResponse line =
                new SaleLineResponse(10L, "Riz blanc", 2, new BigDecimal("2500"), new BigDecimal("5000"));
        return new SaleResponse(id, String.format("V-%04d", id), 1L, "Épicerie Rova", status, paymentStatus,
                new BigDecimal("5000"), BigDecimal.ZERO, PaymentMethod.CASH, null, null, false, List.of(line),
                null, null);
    }
}
