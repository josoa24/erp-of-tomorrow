package com.hackaton.stock.status.service;

import com.hackaton.stock.lot.dto.LotBalance;
import com.hackaton.stock.lot.service.reader.OpenLotReader;
import com.hackaton.stock.movement.dto.ProductOutQuantity;
import com.hackaton.stock.movement.entity.MovementType;
import com.hackaton.stock.movement.repository.StockMovementRepository;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.product.service.reader.AutonomyCalculator;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusQuery;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private final OpenLotReader openLotReader;
    private final StockStatusItemFactory itemFactory;

    @Override
    public List<StockStatusItem> findAll(StockStatusQuery query) {
        Instant since = Instant.now().minus(AutonomyCalculator.ANALYSIS_DAYS, ChronoUnit.DAYS);
        Map<Long, Long> outByProduct = movementRepository.sumQuantityByProductSince(MovementType.OUT, since).stream()
                .collect(Collectors.toMap(ProductOutQuantity::productId, ProductOutQuantity::quantity));
        Map<Long, LotBalance> earliestLots = openLotReader.findEarliestOpenLotByProduct();
        return productRepository.findAll().stream()
                .map(product -> itemFactory.create(product,
                        outByProduct.getOrDefault(product.getId(), 0L), earliestLots.get(product.getId())))
                .filter(query::matches)
                .sorted(MOST_URGENT_FIRST)
                .toList();
    }
}
