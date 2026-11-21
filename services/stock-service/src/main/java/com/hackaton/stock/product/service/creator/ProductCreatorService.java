package com.hackaton.stock.product.service.creator;

import com.hackaton.stock.common.exception.DuplicateResourceException;
import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.mapper.ProductMapper;
import com.hackaton.stock.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductCreatorService implements ProductCreator {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public ProductResponse create(ProductCreateRequest request) {
        if (productRepository.existsBySku(request.sku())) {
            throw new DuplicateResourceException("Un produit avec le SKU " + request.sku() + " existe déjà");
        }
        return productMapper.toResponse(productRepository.save(productMapper.toEntity(request)));
    }
}
