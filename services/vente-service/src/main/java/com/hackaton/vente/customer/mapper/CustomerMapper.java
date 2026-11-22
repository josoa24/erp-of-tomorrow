package com.hackaton.vente.customer.mapper;

import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.entity.CustomerType;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    public Customer toEntity(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return customer;
    }

    public void apply(Customer customer, CustomerRequest request) {
        customer.setName(request.name().trim());
        customer.setPhone(request.phone());
        customer.setEmail(request.email() == null ? null : request.email().trim().toLowerCase());
        customer.setAddress(request.address());
        customer.setCity(request.city());
        customer.setType(request.type() == null ? CustomerType.PARTICULIER : request.type());
        customer.setNotes(request.notes());
        customer.setNif(blankToNull(request.nif()));
        customer.setStat(blankToNull(request.stat()));
        customer.setCreditLimit(request.creditLimit());
    }

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getAddress(),
                customer.getCity(),
                typeOf(customer),
                customer.getNotes(),
                customer.getCreatedAt(),
                customer.getNif(),
                customer.getStat(),
                customer.getCreditLimit());
    }

    public CustomerType typeOf(Customer customer) {
        return customer.getType() == null ? CustomerType.PARTICULIER : customer.getType();
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
