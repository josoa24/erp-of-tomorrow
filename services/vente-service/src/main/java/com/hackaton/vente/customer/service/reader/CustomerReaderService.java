package com.hackaton.vente.customer.service.reader;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerReaderService implements CustomerReader {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
                .map(customerMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Client", id));
    }

    @Override
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(customerMapper::toResponse).toList();
    }
}
