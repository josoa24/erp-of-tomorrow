package com.hackaton.vente.report.controller;

import com.hackaton.vente.report.dto.SalesSummaryResponse;
import com.hackaton.vente.report.dto.TopProductResponse;
import com.hackaton.vente.report.service.SalesSummaryReader;
import com.hackaton.vente.report.service.TopProductsReader;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SalesReportController {

    private final SalesSummaryReader summaryReader;
    private final TopProductsReader topProductsReader;

    @GetMapping("/summary")
    public SalesSummaryResponse summarize(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return summaryReader.summarize(from, to);
    }

    @GetMapping("/top-products")
    public List<TopProductResponse> topProducts(@RequestParam(defaultValue = "5") int limit) {
        return topProductsReader.findTop(limit);
    }
}
