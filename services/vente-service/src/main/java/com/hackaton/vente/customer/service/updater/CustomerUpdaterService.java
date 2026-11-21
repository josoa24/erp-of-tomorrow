package com.hackaton.vente.customer.service.updater;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerUpdaterService implements CustomerUpdater {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
        customerMapper.apply(customer, request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }
}
