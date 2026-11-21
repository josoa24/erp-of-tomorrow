package com.hackaton.stock.purchaseorder.mapper;

import com.hackaton.stock.purchaseorder.dto.PurchaseOrderLineResponse;
import com.hackaton.stock.purchaseorder.dto.PurchaseOrderResponse;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderMapper {

    public PurchaseOrderResponse toResponse(PurchaseOrder order) {
        return new PurchaseOrderResponse(
                order.getId(),
                order.getSupplierId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getLines().stream().map(this::toLineResponse).toList(),
                order.getCreatedAt(),
                order.getValidatedAt(),
                order.getReceivedAt());
    }

    private PurchaseOrderLineResponse toLineResponse(PurchaseOrderLine line) {
        return new PurchaseOrderLineResponse(
                line.getProductId(), line.getProductName(), line.getQuantity(), line.getUnitPrice(),
                line.lineTotal());
    }
}
