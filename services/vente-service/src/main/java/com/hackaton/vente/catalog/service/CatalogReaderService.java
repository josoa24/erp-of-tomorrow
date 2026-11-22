package com.hackaton.vente.catalog.service;

import com.hackaton.vente.catalog.dto.CatalogProductResponse;
import com.hackaton.vente.stock.client.StockCatalogProduct;
import com.hackaton.vente.stock.client.StockClient;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatalogReaderService implements CatalogReader {

    private final StockClient stockClient;

    @Override
    public List<CatalogProductResponse> search(String search) {
        return stockClient.searchProducts(search).stream().map(this::toResponse).toList();
    }

    private CatalogProductResponse toResponse(StockCatalogProduct product) {
        return new CatalogProductResponse(
                product.id(), product.name(), product.sku(), product.unit(), product.unitPrice(), product.quantity());
    }
}
