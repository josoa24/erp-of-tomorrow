package com.hackaton.vente.report.service;

import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopProductsReaderService implements TopProductsReader {

    private static final int MAX_LIMIT = 50;

    private final SaleRepository saleRepository;

    @Override
    public List<TopProductResponse> findTop(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        return ProductSalesAggregation.top(saleRepository.findEffective(), safeLimit);
    }
}
