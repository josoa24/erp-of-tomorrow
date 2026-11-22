package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.replenishment.config.ReplenishmentProperties;
import com.hackaton.stock.replenishment.dto.ForecastConfidence;
import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.stereotype.Component;

/** Calcul pur de la prévision : consommation, stock de sécurité, point de commande et quantité. */
@Component
public class ReplenishmentCalculator {

    public static final double SERVICE_LEVEL_Z = 1.65;
    private static final double EPSILON = 1e-9;

    private final int coverageDays;

    public ReplenishmentCalculator(ReplenishmentProperties properties) {
        this.coverageDays = properties.coverageDays();
    }

    public ReplenishmentForecast calculate(Product product, ReplenishmentInput input) {
        List<Long> outs = input.dailyOuts();
        long total = outs.stream().mapToLong(Long::longValue).sum();
        double mean = (double) total / outs.size();
        double sigma = Math.sqrt(outs.stream().mapToDouble(q -> (q - mean) * (q - mean)).sum() / outs.size());
        LeadTime lead = input.leadTime();
        int stock = product.getQuantity();
        int safetyStock = (int) ceil(SERVICE_LEVEL_Z * sigma * Math.sqrt(lead.days()));
        BigDecimal reorderPoint = round(mean * lead.days() + safetyStock, 2);
        boolean enough = input.enoughHistory();
        Integer recommended = enough
                ? (int) Math.max(0, ceil(mean * (lead.days() + coverageDays) + safetyStock - stock))
                : null;
        boolean needsReorder = enough && total > 0 && BigDecimal.valueOf(stock).compareTo(reorderPoint) <= 0;
        return new ReplenishmentForecast(
                product.getId(),
                product.getName(),
                stock,
                outs.size(),
                round(mean, 2),
                round(sigma, 2),
                lead.days(),
                lead.source(),
                lead.supplierId(),
                safetyStock,
                reorderPoint,
                recommended,
                total == 0 ? null : round(stock / mean, 1),
                needsReorder,
                enough ? ForecastConfidence.OK : ForecastConfidence.INSUFFICIENT_DATA,
                ReplenishmentAssumptions.describe(outs.size(), lead, enough, coverageDays));
    }

    private static long ceil(double value) {
        return (long) Math.ceil(value - EPSILON);
    }

    private static BigDecimal round(double value, int scale) {
        return BigDecimal.valueOf(value).setScale(scale, RoundingMode.HALF_UP);
    }
}
