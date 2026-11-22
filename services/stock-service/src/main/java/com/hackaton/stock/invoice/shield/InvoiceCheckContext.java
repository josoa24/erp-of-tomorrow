package com.hackaton.stock.invoice.shield;

import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.supplier.entity.Supplier;
import java.time.LocalDate;
import java.util.Optional;

/** Données chargées une seule fois et partagées par tous les contrôles d'une facture. */
public record InvoiceCheckContext(
        SupplierInvoiceRequest invoice,
        Optional<Supplier> supplier,
        Optional<PurchaseOrder> purchaseOrder,
        LocalDate today) {
}
