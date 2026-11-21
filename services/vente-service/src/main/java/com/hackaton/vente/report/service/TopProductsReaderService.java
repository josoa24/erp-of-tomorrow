package com.hackaton.vente.report.service;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
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
public class TopProductsReaderService implements TopProductsReader {

    private static final int MAX_LIMIT = 50;

    private final SaleRepository saleRepository;

    @Override
    public List<TopProductResponse> findTop(int limit) {
        int safeLimit = Math.max(1, Math.min(limit, MAX_LIMIT));
        Map<Long, List<SaleLine>> linesByProduct = saleRepository.findByStatus(DocumentStatus.VALIDATED).stream()
                .flatMap(sale -> sale.getLines().stream())
                .collect(Collectors.groupingBy(SaleLine::getProductId));
        return linesByProduct.values().stream()
                .map(this::aggregate)
                .sorted(Comparator.comparingLong(TopProductResponse::totalQuantity).reversed())
                .limit(safeLimit)
                .toList();
    }

    private TopProductResponse aggregate(List<SaleLine> lines) {
        long quantity = lines.stream().mapToLong(SaleLine::getQuantity).sum();
        BigDecimal revenue = lines.stream().map(SaleLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TopProductResponse(lines.get(0).getProductId(), lines.get(0).getProductName(), quantity, revenue);
    }
}
