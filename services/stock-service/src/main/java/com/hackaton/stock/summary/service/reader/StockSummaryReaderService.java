package com.hackaton.stock.summary.service.reader;

import com.hackaton.stock.product.repository.ProductRepository;
import com.hackaton.stock.summary.dto.StockSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockSummaryReaderService implements StockSummaryReader {

    private final ProductRepository productRepository;

    @Override
    public StockSummaryResponse summarize() {
        return new StockSummaryResponse(
                productRepository.count(),
                productRepository.countLowStock(),
                productRepository.totalStockValue());
    }
}
