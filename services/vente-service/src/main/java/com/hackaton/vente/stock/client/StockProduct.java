package com.hackaton.vente.stock.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record StockProduct(Long id, String name, BigDecimal unitPrice, int quantity) {
}
