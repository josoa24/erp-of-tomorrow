package com.hackaton.stock.summary.controller;

import com.hackaton.stock.summary.dto.StockSummaryResponse;
import com.hackaton.stock.summary.service.reader.StockSummaryReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/summary")
@RequiredArgsConstructor
public class StockSummaryController {

    private final StockSummaryReader summaryReader;

    @GetMapping
    public StockSummaryResponse summarize() {
        return summaryReader.summarize();
    }
}
