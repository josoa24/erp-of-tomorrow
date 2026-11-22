package com.hackaton.vente.customer.service;

import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.entity.CustomerType;

final class CustomerEntities {

    private CustomerEntities() {
    }

    static Customer customer(Long id, String name, String phone, String email, String city) {
        Customer customer = new Customer();
        customer.setId(id);
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setCity(city);
        return customer;
    }

    static Customer customer(Long id, String name, CustomerType type) {
        Customer customer = customer(id, name, null, null, null);
        customer.setType(type);
        return customer;
    }
}
