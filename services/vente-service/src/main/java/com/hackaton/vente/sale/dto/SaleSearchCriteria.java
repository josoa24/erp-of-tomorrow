package com.hackaton.vente.sale.dto;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.sale.entity.PaymentMethod;
import java.time.LocalDate;
import java.util.List;

public record SaleSearchCriteria(
        Long customerId,
        DocumentStatus status,
        LocalDate from,
        LocalDate to,
        String search,
        List<PaymentStatus> paymentStatuses,
        PaymentMethod paymentMethod,
        Boolean toDeliver) {

    public static SaleSearchCriteria between(LocalDate from, LocalDate to) {
        return new SaleSearchCriteria(null, null, from, to, null, null, null, null);
    }
}
