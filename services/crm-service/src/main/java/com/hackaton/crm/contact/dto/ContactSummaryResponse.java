package com.hackaton.crm.contact.dto;

import java.math.BigDecimal;

public record ContactSummaryResponse(
        long activeCustomers,
        long totalCustomers,
        long suppliers,
        long openPurchaseOrders,
        long followUp,
        BigDecimal creditOutstanding,
        long overLimitCustomers) {
}
