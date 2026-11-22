package com.hackaton.stock.summary.service.reader;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusQuery;
import com.hackaton.stock.status.service.StockStatusReader;
import com.hackaton.stock.summary.dto.StockSummaryResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockSummaryReaderService implements StockSummaryReader {

    private static final int RATE_SCALE = 4;

    private final ProductRepository productRepository;
    private final StockStatusReader statusReader;

    @Override
    public StockSummaryResponse summarize() {
        long totalProducts = productRepository.count();
        long lowStockProducts = productRepository.countLowStock();
        List<StockStatusItem> items = statusReader.findAll(StockStatusQuery.everything());
        return new StockSummaryResponse(
                totalProducts,
                lowStockProducts,
                productRepository.totalStockValue(),
                availabilityRate(totalProducts, lowStockProducts),
                items.stream().filter(item -> item.lowStock() && item.risk() == StockRisk.CRITICAL).count(),
                productRepository.totalPurchaseValue(),
                productRepository.countByPurchasePriceIsNull(),
                items.stream().filter(StockStatusItem::expiringSoon).count());
    }

    private BigDecimal availabilityRate(long totalProducts, long lowStockProducts) {
        if (totalProducts == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(totalProducts - lowStockProducts)
                .divide(BigDecimal.valueOf(totalProducts), RATE_SCALE, RoundingMode.HALF_UP);
    }
}
