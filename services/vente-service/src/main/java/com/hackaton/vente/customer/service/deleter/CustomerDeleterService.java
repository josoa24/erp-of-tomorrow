package com.hackaton.vente.customer.service.deleter;

import com.hackaton.vente.common.exception.BusinessException;
import com.hackaton.vente.common.exception.ResourceNotFoundException;
import com.hackaton.vente.customer.repository.CustomerRepository;
import com.hackaton.vente.sale.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerDeleterService implements CustomerDeleter {

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Client", id);
        }
        if (saleRepository.existsByCustomerId(id)) {
            throw new BusinessException("Ce client a des ventes et ne peut pas être supprimé");
        }
        customerRepository.deleteById(id);
    }
}
