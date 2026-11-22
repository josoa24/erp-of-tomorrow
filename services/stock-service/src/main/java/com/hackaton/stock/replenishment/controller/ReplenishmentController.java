package com.hackaton.stock.replenishment.controller;

import com.hackaton.stock.replenishment.dto.ReplenishmentForecast;
import com.hackaton.stock.replenishment.service.ReplenishmentReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReplenishmentController {

    private final ReplenishmentReader replenishmentReader;

    @GetMapping("/products/{id}/replenishment")
    public ReplenishmentForecast forProduct(@PathVariable Long id) {
        return replenishmentReader.forProduct(id);
    }

    @GetMapping("/replenishment")
    public List<ReplenishmentForecast> findAll(@RequestParam(defaultValue = "true") boolean onlyNeeded) {
        return replenishmentReader.findAll(onlyNeeded);
    }
}
