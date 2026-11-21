package com.hackaton.stock.supplier.dto;

import java.math.BigDecimal;
import java.util.List;

public record SupplierProfileResponse(
        SupplierResponse supplier,
        long ordersCount,
        long receivedOrdersCount,
        BigDecimal totalOrdered,
        BigDecimal averageReceptionDays,
        List<SuppliedProduct> suppliedProducts,
        List<RecentOrder> recentOrders) {
}
