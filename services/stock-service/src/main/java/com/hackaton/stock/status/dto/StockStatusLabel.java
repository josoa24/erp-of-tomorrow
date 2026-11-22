package com.hackaton.stock.status.dto;

import com.hackaton.stock.product.dto.StockRisk;

public enum StockStatusLabel {
    RUPTURE_RISK,
    BELOW_THRESHOLD,
    IN_STOCK;

    public static StockStatusLabel of(StockRisk risk, boolean lowStock) {
        if (risk == StockRisk.CRITICAL) {
            return RUPTURE_RISK;
        }
        return lowStock ? BELOW_THRESHOLD : IN_STOCK;
    }
}
