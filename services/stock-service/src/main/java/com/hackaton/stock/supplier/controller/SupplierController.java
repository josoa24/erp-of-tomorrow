package com.hackaton.stock.supplier.controller;

import com.hackaton.stock.supplier.dto.SupplierRequest;
import com.hackaton.stock.supplier.dto.SupplierResponse;
import com.hackaton.stock.supplier.service.creator.SupplierCreator;
import com.hackaton.stock.supplier.service.deleter.SupplierDeleter;
import com.hackaton.stock.supplier.service.reader.SupplierReader;
import com.hackaton.stock.supplier.service.updater.SupplierUpdater;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierReader supplierReader;
    private final SupplierCreator supplierCreator;
    private final SupplierUpdater supplierUpdater;
    private final SupplierDeleter supplierDeleter;

    @GetMapping
    public List<SupplierResponse> findAll() {
        return supplierReader.findAll();
    }

    @GetMapping("/{id}")
    public SupplierResponse findById(@PathVariable Long id) {
        return supplierReader.findById(id);
    }

    @PostMapping
    public ResponseEntity<SupplierResponse> create(@Valid @RequestBody SupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierCreator.create(request));
    }

    @PutMapping("/{id}")
    public SupplierResponse update(@PathVariable Long id, @Valid @RequestBody SupplierRequest request) {
        return supplierUpdater.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        supplierDeleter.delete(id);
    }
}
