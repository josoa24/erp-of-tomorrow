package com.hackaton.vente.customer.controller;

import com.hackaton.vente.customer.dto.CustomerProfileResponse;
import com.hackaton.vente.customer.service.profile.CustomerProfileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers/{id}/profile")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileReader profileReader;

    @GetMapping
    public CustomerProfileResponse findProfile(@PathVariable Long id) {
        return profileReader.findProfile(id);
    }
}
