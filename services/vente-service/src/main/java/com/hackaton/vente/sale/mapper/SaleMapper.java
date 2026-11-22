package com.hackaton.vente.sale.mapper;

import com.hackaton.vente.sale.dto.SaleLineResponse;
import com.hackaton.vente.sale.dto.SaleResponse;
import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.entity.SaleLine;
import com.hackaton.vente.sale.service.facts.SaleFacts;
import com.hackaton.vente.sale.service.status.PaymentStatusResolver;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SaleMapper {

    private final PaymentStatusResolver statusResolver;

    public SaleResponse toResponse(Sale sale, SaleFacts facts) {
        BigDecimal paidAmount = facts.paidAmount(sale);
        return new SaleResponse(
                sale.getId(),
                SaleNumber.of(sale.getId()),
                sale.getCustomerId(),
                facts.customerName(sale),
                sale.getStatus(),
                statusResolver.resolve(sale, paidAmount, facts.isCancelled(sale)),
                sale.getTotalAmount(),
                paidAmount,
                sale.getPaymentMethod(),
                sale.getCreditDays(),
                statusResolver.dueDate(sale),
                sale.isToDeliver(),
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
