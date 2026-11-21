package com.hackaton.stock.product.controller;

import com.hackaton.stock.product.dto.AutonomyResponse;
import com.hackaton.stock.product.service.reader.ProductAutonomyReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products/{id}/autonomy")
@RequiredArgsConstructor
public class ProductAutonomyController {

    private final ProductAutonomyReader autonomyReader;

    @GetMapping
    public AutonomyResponse analyze(@PathVariable Long id) {
        return autonomyReader.analyze(id);
    }
}
