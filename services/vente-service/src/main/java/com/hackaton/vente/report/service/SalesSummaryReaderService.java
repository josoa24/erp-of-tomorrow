package com.hackaton.vente.report.service;

import com.hackaton.vente.common.status.DocumentStatus;
import com.hackaton.vente.report.dto.DailyRevenue;
import com.hackaton.vente.report.dto.SalesSummaryResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.repository.SaleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesSummaryReaderService implements SalesSummaryReader {

    private final SaleRepository saleRepository;

    @Override
    public SalesSummaryResponse summarize(LocalDate from, LocalDate to) {
        List<Sale> sales = saleRepository.findByStatus(DocumentStatus.VALIDATED).stream()
                .filter(sale -> from == null || !dayOf(sale).isBefore(from))
                .filter(sale -> to == null || !dayOf(sale).isAfter(to))
                .toList();
        BigDecimal total = sum(sales);
        BigDecimal average = sales.isEmpty()
                ? BigDecimal.ZERO
                : total.divide(BigDecimal.valueOf(sales.size()), 2, RoundingMode.HALF_UP);
        return new SalesSummaryResponse(sales.size(), total, average, dailyBreakdown(sales));
    }

    private List<DailyRevenue> dailyBreakdown(List<Sale> sales) {
        Map<LocalDate, List<Sale>> byDay = sales.stream()
                .collect(Collectors.groupingBy(this::dayOf, TreeMap::new, Collectors.toList()));
        return byDay.entrySet().stream()
                .map(entry -> new DailyRevenue(entry.getKey(), entry.getValue().size(), sum(entry.getValue())))
                .toList();
    }

    private BigDecimal sum(List<Sale> sales) {
        return sales.stream().map(Sale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private LocalDate dayOf(Sale sale) {
        return sale.getValidatedAt().atZone(ZoneOffset.UTC).toLocalDate();
    }
}
