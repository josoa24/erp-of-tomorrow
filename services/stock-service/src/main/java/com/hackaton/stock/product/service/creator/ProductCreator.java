package com.hackaton.stock.product.service.creator;

import com.hackaton.stock.product.dto.ProductCreateRequest;
import com.hackaton.stock.product.dto.ProductResponse;

public interface ProductCreator {

    ProductResponse create(ProductCreateRequest request);
}
