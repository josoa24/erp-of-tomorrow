package com.hackaton.vente.sale.service.facts;

import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public record SaleFacts(Map<Long, String> customerNames, Map<Long, BigDecimal> paidBySale, Set<Long> cancelledSaleIds) {

    public static final String WALK_IN_CUSTOMER = "Client comptoir";

    public static SaleFacts none() {
        return new SaleFacts(Map.of(), Map.of(), Set.of());
    }

    public String customerName(Sale sale) {
        return sale.getCustomerId() == null ? WALK_IN_CUSTOMER : customerNames.get(sale.getCustomerId());
    }

    public BigDecimal paidAmount(Sale sale) {
        return paidBySale.getOrDefault(sale.getId(), BigDecimal.ZERO);
    }

    public boolean isCancelled(Sale sale) {
        return cancelledSaleIds.contains(sale.getId());
    }
}
