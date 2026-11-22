package com.hackaton.stock.status.dto;

import com.hackaton.stock.common.util.SearchText;
import com.hackaton.stock.product.dto.StockRisk;

public record StockStatusQuery(StockRisk risk, String search, Boolean lowStock, Integer expiringWithinDays) {

    public static StockStatusQuery everything() {
        return new StockStatusQuery(null, null, null, null);
    }

    public static StockStatusQuery searching(String search) {
        return new StockStatusQuery(null, search, null, null);
    }

    public boolean matches(StockStatusItem item) {
        return (risk == null || item.risk() == risk)
                && (lowStock == null || item.lowStock() == lowStock)
                && matchesExpiry(item)
                && SearchText.anyContains(search, item.productName(), item.sku(), item.category());
    }

    private boolean matchesExpiry(StockStatusItem item) {
        return expiringWithinDays == null
                || (item.daysToExpiry() != null && item.daysToExpiry() <= expiringWithinDays);
    }
}
