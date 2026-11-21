package com.hackaton.stock.product.service.updater;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
import com.hackaton.stock.product.entity.Product;
import com.hackaton.stock.product.mapper.ProductMapper;
import com.hackaton.stock.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductUpdaterService implements ProductUpdater {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        productMapper.applyUpdate(product, request);
        return productMapper.toResponse(productRepository.save(product));
    }
}
