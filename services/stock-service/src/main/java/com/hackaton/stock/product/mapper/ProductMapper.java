package com.hackaton.stock.product.mapper;

import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
import com.hackaton.stock.product.entity.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toEntity(ProductCreateRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setSku(request.sku());
        product.setUnit(request.unit());
        product.setQuantity(request.quantity());
        product.setMinThreshold(request.minThreshold());
        product.setUnitPrice(request.unitPrice());
        return product;
    }

    public void applyUpdate(Product product, ProductUpdateRequest request) {
        product.setName(request.name());
        product.setUnit(request.unit());
        product.setMinThreshold(request.minThreshold());
        product.setUnitPrice(request.unitPrice());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getUnit(),
                product.getQuantity(),
                product.getMinThreshold(),
                product.getUnitPrice(),
                product.getQuantity() <= product.getMinThreshold(),
                product.getCreatedAt());
    }
}
