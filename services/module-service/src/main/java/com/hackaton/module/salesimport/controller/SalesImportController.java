package com.hackaton.module.salesimport.controller;

import com.hackaton.module.common.security.Caller;
import com.hackaton.module.salesimport.dto.SalesImportAvailability;
import com.hackaton.module.salesimport.dto.SalesImportResult;
import com.hackaton.module.salesimport.service.SalesImportCreator;
import com.hackaton.module.salesimport.service.SalesImportReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/definitions/{slug}/sales-import")
@RequiredArgsConstructor
public class SalesImportController {

    private final SalesImportReader importReader;
    private final SalesImportCreator importCreator;

    @GetMapping
    public SalesImportAvailability availability(@PathVariable String slug) {
        return importReader.availability(slug);
    }

    @PostMapping
    public SalesImportResult importSales(@PathVariable String slug, Caller caller) {
        return importCreator.importSales(slug, caller);
    }
}
