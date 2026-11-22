package com.hackaton.vente.customer.controller;

import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.CustomerType;
import java.math.BigDecimal;

final class CustomerFixtures {

    static final String VALID_BODY = "{\"name\":\"Epicerie Rakoto\",\"email\":\"rakoto@mail.mg\",\"type\":\"ENTREPRISE\"}";

    private CustomerFixtures() {
    }

    static CustomerResponse customer(Long id, String name) {
        return new CustomerResponse(id, name, null, null, null, null, CustomerType.ENTREPRISE, null, null,
                "4000123456", "47111 11 2020 0 00123", new BigDecimal("2000000.00"));
    }
}
