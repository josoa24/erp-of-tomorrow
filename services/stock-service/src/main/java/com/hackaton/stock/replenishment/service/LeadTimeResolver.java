package com.hackaton.stock.replenishment.service;

import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.replenishment.config.ReplenishmentProperties;
import com.hackaton.stock.replenishment.dto.LeadTimeSource;
import com.hackaton.stock.supplier.entity.Supplier;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Délai du fournisseur du dernier bon contenant le produit, sinon la valeur par défaut configurée. */
@Component
@RequiredArgsConstructor
public class LeadTimeResolver {

    private final PurchaseOrderRepository orderRepository;
    private final SupplierRepository supplierRepository;
    private final ReplenishmentProperties properties;

    public LeadTime resolve(Long productId) {
        Long supplierId = orderRepository.findFirstByLinesProductIdOrderByCreatedAtDesc(productId)
                .map(PurchaseOrder::getSupplierId)
                .orElse(null);
        Integer days = supplierId == null ? null : supplierRepository.findById(supplierId)
                .map(Supplier::getUsualDeliveryDays)
                .orElse(null);
        if (days == null) {
            return new LeadTime(properties.defaultLeadDays(), LeadTimeSource.DEFAULT, supplierId);
        }
        return new LeadTime(days, LeadTimeSource.SUPPLIER, supplierId);
    }
}
