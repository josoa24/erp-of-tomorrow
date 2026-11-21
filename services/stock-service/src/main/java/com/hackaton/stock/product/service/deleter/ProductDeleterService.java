package com.hackaton.stock.product.service.deleter;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductDeleterService implements ProductDeleter {

    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Produit", id);
        }
        productRepository.deleteById(id);
    }
}
