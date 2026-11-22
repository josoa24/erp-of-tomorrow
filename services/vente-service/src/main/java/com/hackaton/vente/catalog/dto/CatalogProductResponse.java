package com.hackaton.vente.catalog.dto;

import java.math.BigDecimal;

public record CatalogProductResponse(
        Long id, String name, String sku, String unit, BigDecimal unitPrice, int quantity) {
}
