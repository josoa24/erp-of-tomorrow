package com.hackaton.vente.catalog.controller;

import com.hackaton.vente.catalog.dto.CatalogProductResponse;
import com.hackaton.vente.catalog.service.CatalogReader;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogReader catalogReader;

    @GetMapping("/products")
    public List<CatalogProductResponse> search(@RequestParam(required = false) String search) {
        return catalogReader.search(search);
    }
}
