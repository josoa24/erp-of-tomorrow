package com.hackaton.stock.lot.controller;

import com.hackaton.stock.lot.dto.LotResponse;
import com.hackaton.stock.lot.service.reader.StockLotReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/{productId}/lots")
@RequiredArgsConstructor
public class StockLotController {

    private final StockLotReader lotReader;

    @GetMapping
    public List<LotResponse> findByProduct(@PathVariable Long productId) {
        return lotReader.findByProductId(productId);
    }
}
