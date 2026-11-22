package com.hackaton.vente.kpi.service.sales;

import com.hackaton.vente.common.time.LocalDays;
import com.hackaton.vente.sale.entity.Sale;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public record SalesTally(long count, BigDecimal revenue) {

    public static SalesTally of(List<Sale> sales, LocalDate from, LocalDate to, ZoneId zone) {
        List<Sale> inPeriod = sales.stream()
                .filter(sale -> LocalDays.isWithin(sale.getValidatedAt(), from, to, zone))
                .toList();
        BigDecimal revenue = inPeriod.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SalesTally(inPeriod.size(), revenue);
    }

    public BigDecimal averageBasket() {
        return count == 0 ? BigDecimal.ZERO : revenue.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
    }
}
