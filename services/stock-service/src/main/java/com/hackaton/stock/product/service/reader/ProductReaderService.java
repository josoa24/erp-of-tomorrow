package com.hackaton.stock.product.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.mapper.ProductMapper;
import com.hackaton.stock.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductReaderService implements ProductReader {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse findById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
    }

    @Override
    public List<ProductResponse> search(String search, Boolean lowStock) {
        String pattern = "%" + (search == null ? "" : search.trim()) + "%";
        return productRepository.search(pattern).stream()
                .map(productMapper::toResponse)
                .filter(product -> lowStock == null || product.lowStock() == lowStock)
                .toList();
    }

    @Override
    public List<ProductResponse> findLowStock() {
        return productRepository.findLowStock().stream().map(productMapper::toResponse).toList();
    }
}
