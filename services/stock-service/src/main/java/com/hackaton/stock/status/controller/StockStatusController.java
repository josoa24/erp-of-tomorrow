package com.hackaton.stock.status.controller;

import com.hackaton.stock.product.dto.StockRisk;
import com.hackaton.stock.status.dto.StockStatusItem;
import com.hackaton.stock.status.service.StockStatusReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StockStatusController {

    private final StockStatusReader statusReader;

    @GetMapping
    public List<StockStatusItem> findAll(@RequestParam(required = false) StockRisk risk) {
        return statusReader.findAll(risk);
    }
}
