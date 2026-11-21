package com.hackaton.vente.customer.service.reader;

import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.common.page.PageResult;
import com.hackaton.vente.customer.dto.CustomerResponse;
import com.hackaton.vente.customer.entity.Customer;
import com.hackaton.vente.customer.mapper.CustomerMapper;
import com.hackaton.vente.customer.repository.CustomerRepository;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
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
    public PageResult<CustomerResponse> search(String search, Integer page, Integer size) {
        String term = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        List<CustomerResponse> matches = customerRepository.findAllByOrderByNameAsc().stream()
                .filter(customer -> matches(customer, term))
                .map(customerMapper::toResponse)
                .toList();
        return PageResult.of(matches, page, size);
    }

    private boolean matches(Customer customer, String term) {
        if (term.isEmpty()) {
            return true;
        }
        return Stream.of(customer.getName(), customer.getPhone(), customer.getEmail(), customer.getCity())
                .anyMatch(value -> value != null && value.toLowerCase(Locale.ROOT).contains(term));
    }
}
