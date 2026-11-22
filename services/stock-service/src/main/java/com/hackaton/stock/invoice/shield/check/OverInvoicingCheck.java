package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Surfacturation : pour chaque produit, la quantité facturée (factures non rejetées du même bon + celle-ci)
 * ne peut pas dépasser la quantité commandée.
 */
@Component
@RequiredArgsConstructor
public class OverInvoicingCheck implements InvoiceCheck {

    private final SupplierInvoiceRepository invoiceRepository;

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        if (context.purchaseOrder().isEmpty()) {
            return List.of();
        }
        PurchaseOrder order = context.purchaseOrder().get();
        Map<Long, Integer> ordered = new HashMap<>();
        order.getLines().forEach(line -> ordered.merge(line.getProductId(), line.getQuantity(), Integer::sum));
        Map<Long, Integer> invoiced = alreadyInvoiced(order.getId());
        context.invoice().lines().stream()
                .filter(line -> line.productId() != null)
                .forEach(line -> invoiced.merge(line.productId(), line.quantity(), Integer::sum));
        List<ShieldAlert> alerts = new ArrayList<>();
        invoiced.forEach((productId, quantity) -> {
            Integer allowed = ordered.get(productId);
            if (allowed != null && quantity > allowed) {
                alerts.add(ShieldAlert.blocking(AlertCode.OVER_INVOICED, "Produit " + productId + " : " + quantity
                        + " unités facturées au total pour " + allowed + " commandées"));
            }
        });
        return alerts;
    }

    private Map<Long, Integer> alreadyInvoiced(Long purchaseOrderId) {
        Map<Long, Integer> invoiced = new HashMap<>();
        invoiceRepository.findByPurchaseOrderIdAndStatusNot(purchaseOrderId, InvoiceStatus.REJECTED).stream()
                .flatMap(invoice -> invoice.getLines().stream())
                .filter(line -> line.getProductId() != null)
                .forEach(line -> invoiced.merge(line.getProductId(), line.getQuantity(), Integer::sum));
        return invoiced;
    }
}
