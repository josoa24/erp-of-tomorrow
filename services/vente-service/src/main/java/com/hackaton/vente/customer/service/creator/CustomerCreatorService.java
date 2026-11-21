package com.hackaton.vente.customer.service.creator;

import com.hackaton.vente.customer.dto.CustomerRequest;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerCreatorService implements CustomerCreator {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        return customerMapper.toResponse(customerRepository.save(customerMapper.toEntity(request)));
    }
}
