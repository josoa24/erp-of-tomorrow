package com.hackaton.stock.product.service.reader;

import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

@Component
public class AutonomyCalculator {

    public static final int ANALYSIS_DAYS = 7;
    private static final BigDecimal CRITICAL_DAYS = BigDecimal.valueOf(3);
    private static final BigDecimal WARNING_DAYS = BigDecimal.valueOf(7);

    public AutonomyResponse calculate(Product product, long outQuantityOverWindow) {
        BigDecimal averageDailyOut = BigDecimal.valueOf(outQuantityOverWindow)
                .divide(BigDecimal.valueOf(ANALYSIS_DAYS), 2, RoundingMode.HALF_UP);
        if (outQuantityOverWindow == 0) {
            return build(product, averageDailyOut, null, StockRisk.OK);
        }
        BigDecimal daysRemaining = BigDecimal.valueOf((long) product.getQuantity() * ANALYSIS_DAYS)
                .divide(BigDecimal.valueOf(outQuantityOverWindow), 1, RoundingMode.HALF_UP);
        return build(product, averageDailyOut, daysRemaining, riskFor(daysRemaining));
    }

    private StockRisk riskFor(BigDecimal daysRemaining) {
        if (daysRemaining.compareTo(CRITICAL_DAYS) < 0) {
            return StockRisk.CRITICAL;
        }
        if (daysRemaining.compareTo(WARNING_DAYS) < 0) {
            return StockRisk.WARNING;
        }
        return StockRisk.OK;
    }

    private AutonomyResponse build(Product product, BigDecimal average, BigDecimal days, StockRisk risk) {
        return new AutonomyResponse(
                product.getId(), product.getName(), product.getQuantity(), ANALYSIS_DAYS, average, days, risk);
    }
}
