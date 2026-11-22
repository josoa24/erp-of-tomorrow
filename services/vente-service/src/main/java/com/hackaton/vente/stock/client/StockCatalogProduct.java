package com.hackaton.vente.stock.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockCatalogProduct(Long id, String name, String sku, String unit, BigDecimal unitPrice, int quantity) {
}
