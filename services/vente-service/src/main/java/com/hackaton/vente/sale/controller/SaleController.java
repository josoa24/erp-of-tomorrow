package com.hackaton.vente.sale.controller;

import com.hackaton.vente.sale.dto.SaleRequest;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.service.creator.SaleCreator;
import com.hackaton.vente.sale.service.deleter.SaleDeleter;
import com.hackaton.vente.sale.service.reader.SaleReader;
import com.hackaton.vente.sale.service.updater.SaleUpdater;
import com.hackaton.vente.sale.service.validator.SaleValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleReader saleReader;
    private final SaleCreator saleCreator;
    private final SaleUpdater saleUpdater;
    private final SaleDeleter saleDeleter;
    private final SaleValidator saleValidator;

    @GetMapping("/{id}")
    public SaleResponse findById(@PathVariable Long id) {
        return saleReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<SaleResponse> create(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(saleCreator.create(request));
    }

    @PutMapping("/{id}")
    public SaleResponse update(@PathVariable Long id, @Valid @RequestBody SaleRequest request) {
        return saleUpdater.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        saleDeleter.delete(id);
    }

    @PostMapping("/{id}/validate")
    public SaleResponse validate(@PathVariable Long id) {
        return saleValidator.validate(id);
    }
}
