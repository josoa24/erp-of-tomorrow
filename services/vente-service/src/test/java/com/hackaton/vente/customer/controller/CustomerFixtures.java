package com.hackaton.vente.customer.controller;

import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.CustomerType;

final class CustomerFixtures {

    static final String VALID_BODY = "{\"name\":\"Epicerie Rakoto\",\"email\":\"rakoto@mail.mg\",\"type\":\"ENTREPRISE\"}";

    private CustomerFixtures() {
    }

    static CustomerResponse customer(Long id, String name) {
        return new CustomerResponse(id, name, null, null, null, null, CustomerType.ENTREPRISE, null, null);
    }
}
