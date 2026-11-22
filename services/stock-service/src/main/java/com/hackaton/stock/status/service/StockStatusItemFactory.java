package com.hackaton.stock.status.service;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.service.expiry.ExpiryPolicy;
import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusLabel;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockStatusItemFactory {

    private final AutonomyCalculator calculator;
    private final ExpiryPolicy expiryPolicy;

    public StockStatusItem create(Product product, long outQuantity, LotBalance earliestOpenLot) {
        AutonomyResponse autonomy = calculator.calculate(product, outQuantity);
        boolean lowStock = product.getQuantity() <= product.getMinThreshold();
        StockRisk risk = finalRisk(autonomy.risk(), product.getQuantity(), lowStock);
        LocalDate expiryDate = earliestOpenLot == null ? null : earliestOpenLot.expiryDate();
        Integer daysToExpiry = expiryPolicy.daysToExpiry(expiryDate);
        return new StockStatusItem(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getUnit(),
                product.getQuantity(),
                product.getMinThreshold(),
                lowStock,
                autonomy.averageDailyOut(),
                autonomy.daysRemaining(),
                risk,
                product.getCategory(),
                product.getPurchasePrice(),
                StockStatusLabel.of(risk, lowStock),
                earliestOpenLot == null ? null : earliestOpenLot.lotNumber(),
                expiryDate,
                daysToExpiry,
                expiryPolicy.isExpiringSoon(daysToExpiry));
    }

    private StockRisk finalRisk(StockRisk autonomyRisk, int quantity, boolean lowStock) {
        if (quantity == 0) {
            return StockRisk.CRITICAL;
        }
        if (autonomyRisk == StockRisk.OK && lowStock) {
            return StockRisk.WARNING;
        }
        return autonomyRisk;
    }
}
