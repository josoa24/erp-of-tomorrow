package com.hackaton.vente.sale.service;

import com.hackaton.vente.sale.dto.SaleLineRequest;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.stock.client.StockClient;
import com.hackaton.vente.stock.client.StockProduct;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleLineFactory {

    private final StockClient stockClient;

    public List<SaleLine> build(List<SaleLineRequest> requests) {
        return requests.stream().map(this::toLine).toList();
    }

    public BigDecimal total(List<SaleLine> lines) {
        return lines.stream().map(SaleLine::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private SaleLine toLine(SaleLineRequest request) {
        StockProduct product = stockClient.getProduct(request.productId());
        SaleLine line = new SaleLine();
        line.setProductId(product.id());
        line.setProductName(product.name());
        line.setUnitPrice(product.unitPrice());
        line.setQuantity(request.quantity());
        return line;
    }
}
