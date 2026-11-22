package com.hackaton.stock.status.service;

import com.hackaton.stock.status.dto.StockStatusCounts;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.dto.StockStatusLabel;
import com.hackaton.stock.status.dto.StockStatusQuery;
import java.util.List;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockStatusCountsReaderService implements StockStatusCountsReader {

    private final StockStatusReader statusReader;

    @Override
    public StockStatusCounts count(String search) {
        List<StockStatusItem> items = statusReader.findAll(StockStatusQuery.searching(search));
        return new StockStatusCounts(
                items.size(),
                countWhere(items, StockStatusItem::lowStock),
                countWhere(items, item -> item.statusLabel() == StockStatusLabel.RUPTURE_RISK),
                countWhere(items, StockStatusItem::expiringSoon));
    }

    private long countWhere(List<StockStatusItem> items, Predicate<StockStatusItem> condition) {
        return items.stream().filter(condition).count();
    }
}
