package com.hackaton.stock.product.service.updater;

import com.hackaton.stock.product.dto.ProductResponse;
import com.hackaton.stock.product.dto.ProductUpdateRequest;

public interface ProductUpdater {

    ProductResponse update(Long id, ProductUpdateRequest request);
}
