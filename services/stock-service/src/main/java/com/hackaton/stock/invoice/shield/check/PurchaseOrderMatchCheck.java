package com.hackaton.stock.invoice.shield.check;

import com.hackaton.stock.invoice.dto.InvoiceLineRequest;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.AlertCode;
import com.hackaton.stock.invoice.shield.InvoiceCheck;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Rapprochement avec le bon de commande : il doit exister, être validé, appartenir au même fournisseur ;
 * chaque produit facturé doit y figurer, à un prix proche de celui commandé (tolérance 2 %).
 */
@Component
public class PurchaseOrderMatchCheck implements InvoiceCheck {

    static final BigDecimal PRICE_TOLERANCE = new BigDecimal("1.02");

    @Override
    public List<ShieldAlert> check(InvoiceCheckContext context) {
        SupplierInvoiceRequest invoice = context.invoice();
        if (invoice.purchaseOrderId() == null) {
            return List.of();
        }
        if (context.purchaseOrder().isEmpty()) {
            return List.of(mismatch("Bon de commande " + invoice.purchaseOrderId() + " introuvable"));
        }
        PurchaseOrder order = context.purchaseOrder().get();
        if (!order.getSupplierId().equals(invoice.supplierId())) {
            return List.of(mismatch("Le bon de commande appartient à un autre fournisseur"));
        }
        if (order.getStatus() == OrderStatus.DRAFT) {
            return List.of(mismatch("Le bon de commande n'est pas validé"));
        }
        List<ShieldAlert> alerts = new ArrayList<>();
        invoice.lines().forEach(line -> checkLine(order, line, alerts));
        return alerts;
    }

    private void checkLine(PurchaseOrder order, InvoiceLineRequest line, List<ShieldAlert> alerts) {
        if (line.productId() == null) {
            alerts.add(ShieldAlert.warning(AlertCode.PURCHASE_ORDER_MISMATCH,
                    "Ligne « " + line.description() + " » sans produit : rapprochement impossible"));
            return;
        }
        Optional<PurchaseOrderLine> ordered = order.getLines().stream()
                .filter(orderLine -> orderLine.getProductId().equals(line.productId()))
                .findFirst();
        if (ordered.isEmpty()) {
            alerts.add(mismatch("Le produit " + line.productId() + " n'a pas été commandé dans ce bon"));
        } else if (line.unitPrice().compareTo(ordered.get().getUnitPrice().multiply(PRICE_TOLERANCE)) > 0) {
            alerts.add(ShieldAlert.warning(AlertCode.PURCHASE_ORDER_MISMATCH,
                    "Prix facturé " + line.unitPrice().stripTrailingZeros().toPlainString() + " Ar supérieur au prix "
                            + "commandé " + ordered.get().getUnitPrice().stripTrailingZeros().toPlainString()
                            + " Ar pour le produit " + line.productId()));
        }
    }

    private static ShieldAlert mismatch(String message) {
        return ShieldAlert.blocking(AlertCode.PURCHASE_ORDER_MISMATCH, message);
    }
}
