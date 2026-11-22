package com.hackaton.stock.status;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusLabel;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class StatusItemFixtures {

    public static final LocalDate TODAY = LocalDate.of(2026, 11, 22);

    private StatusItemFixtures() {
    }

    public static StockStatusItem item(long id, String name, String category, StockRisk risk, boolean lowStock,
                                       Integer daysToExpiry) {
        boolean hasLot = daysToExpiry != null;
        return new StockStatusItem(id, name, "SKU-" + id, "sac", 10, 5, lowStock, BigDecimal.ONE, BigDecimal.TEN,
                risk, category, null, StockStatusLabel.of(risk, lowStock),
                hasLot ? "L-" + id : null, hasLot ? TODAY.plusDays(daysToExpiry) : null, daysToExpiry,
                hasLot && daysToExpiry <= 30);
    }

    public static StockStatusItem item(long id, String name, StockRisk risk, boolean lowStock) {
        return item(id, name, null, risk, lowStock, null);
    }
}
