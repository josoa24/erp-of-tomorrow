package com.hackaton.stock.status.service;

import com.hackaton.stock.movement.dto.ProductOutQuantity;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockStatusReaderService implements StockStatusReader {

    private static final Comparator<StockStatusItem> MOST_URGENT_FIRST = Comparator
            .comparing(StockStatusItem::risk, Comparator.reverseOrder())
            .thenComparing(StockStatusItem::daysRemaining, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(StockStatusItem::productName);

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final AutonomyCalculator calculator;

    @Override
    public List<StockStatusItem> findAll(StockRisk risk) {
        Instant since = Instant.now().minus(AutonomyCalculator.ANALYSIS_DAYS, ChronoUnit.DAYS);
        Map<Long, Long> outByProduct = movementRepository.sumQuantityByProductSince(MovementType.OUT, since).stream()
                .collect(Collectors.toMap(ProductOutQuantity::productId, ProductOutQuantity::quantity));
        return productRepository.findAll().stream()
                .map(product -> toItem(product, outByProduct.getOrDefault(product.getId(), 0L)))
                .filter(item -> risk == null || item.risk() == risk)
                .sorted(MOST_URGENT_FIRST)
                .toList();
    }

    private StockStatusItem toItem(Product product, long outQuantity) {
        AutonomyResponse autonomy = calculator.calculate(product, outQuantity);
        boolean lowStock = product.getQuantity() <= product.getMinThreshold();
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
                finalRisk(autonomy.risk(), product.getQuantity(), lowStock));
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
