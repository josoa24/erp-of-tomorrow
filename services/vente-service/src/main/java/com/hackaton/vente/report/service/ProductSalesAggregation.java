package com.hackaton.vente.report.service;

import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class ProductSalesAggregation {

    private ProductSalesAggregation() {
    }

    public static List<TopProductResponse> top(List<Sale> sales, int limit) {
        Map<Long, List<SaleLine>> linesByProduct = sales.stream()
                .flatMap(sale -> sale.getLines().stream())
                .collect(Collectors.groupingBy(SaleLine::getProductId));
        return linesByProduct.values().stream()
                .map(ProductSalesAggregation::aggregate)
                .sorted(Comparator.comparingLong(TopProductResponse::totalQuantity).reversed())
                .limit(limit)
                .toList();
    }

    private static TopProductResponse aggregate(List<SaleLine> lines) {
        long quantity = lines.stream().mapToLong(SaleLine::getQuantity).sum();
        BigDecimal revenue = lines.stream().map(SaleLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new TopProductResponse(lines.get(0).getProductId(), lines.get(0).getProductName(), quantity, revenue);
    }
}
