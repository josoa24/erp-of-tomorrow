package com.hackaton.vente.customer.repository;

import com.hackaton.vente.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
