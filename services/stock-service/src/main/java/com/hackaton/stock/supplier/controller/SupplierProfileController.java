package com.hackaton.stock.supplier.controller;

import com.hackaton.stock.supplier.dto.SupplierProfileResponse;
import com.hackaton.stock.supplier.service.profile.SupplierProfileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/suppliers/{id}/profile")
@RequiredArgsConstructor
public class SupplierProfileController {

    private final SupplierProfileReader profileReader;

    @GetMapping
    public SupplierProfileResponse findProfile(@PathVariable Long id) {
        return profileReader.findProfile(id);
    }
}
