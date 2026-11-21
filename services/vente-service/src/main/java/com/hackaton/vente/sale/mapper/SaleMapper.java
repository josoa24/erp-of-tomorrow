package com.hackaton.vente.sale.mapper;

import com.hackaton.vente.sale.dto.SaleLineResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import org.springframework.stereotype.Component;

@Component
public class SaleMapper {

    public SaleResponse toResponse(Sale sale) {
        return new SaleResponse(
                sale.getId(),
                sale.getCustomerId(),
                sale.getStatus(),
                sale.getTotalAmount(),
                sale.getLines().stream().map(this::toLineResponse).toList(),
                sale.getCreatedAt(),
                sale.getValidatedAt());
    }

    private SaleLineResponse toLineResponse(SaleLine line) {
        return new SaleLineResponse(
                line.getProductId(), line.getProductName(), line.getQuantity(), line.getUnitPrice(),
                line.lineTotal());
    }
}
