package com.hackaton.stock.status.controller;

import com.hackaton.stock.status.dto.StockStatusCounts;
import com.hackaton.stock.status.service.StockStatusCountsReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status/counts")
@RequiredArgsConstructor
public class StockStatusCountsController {

    private final StockStatusCountsReader countsReader;

    @GetMapping
    public StockStatusCounts count(@RequestParam(required = false) String search) {
        return countsReader.count(search);
    }
}
