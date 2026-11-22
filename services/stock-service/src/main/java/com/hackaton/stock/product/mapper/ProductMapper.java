package com.hackaton.stock.product.mapper;

import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;
import com.hackaton.stock.product.entity.Product;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
        product.setCategory(categoryOrNull(request.category()));
        product.setPurchasePrice(request.purchasePrice());
        return product;
    }

    public void applyUpdate(Product product, ProductUpdateRequest request) {
        product.setName(request.name());
        product.setUnit(request.unit());
        product.setMinThreshold(request.minThreshold());
        product.setUnitPrice(request.unitPrice());
        product.setCategory(categoryOrNull(request.category()));
        product.setPurchasePrice(request.purchasePrice());
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
                product.getCreatedAt(),
                product.getCategory(),
                product.getPurchasePrice());
    }

    private String categoryOrNull(String category) {
        return StringUtils.hasText(category) ? category.trim() : null;
    }
}
