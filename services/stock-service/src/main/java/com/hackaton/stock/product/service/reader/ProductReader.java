package com.hackaton.stock.product.service.reader;

import com.hackaton.stock.product.dto.ProductResponse;
import java.util.List;

public interface ProductReader {

    ProductResponse findById(Long id);

    List<ProductResponse> search(String search, Boolean lowStock);

    List<ProductResponse> findLowStock();
}
