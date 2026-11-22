package com.hackaton.stock.invoice;

import com.hackaton.stock.invoice.dto.InvoiceLineRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.shield.InvoiceCheckContext;
import com.hackaton.stock.purchaseorder.entity.OrderStatus;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrderLine;
import com.hackaton.stock.supplier.entity.Supplier;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Données de test partagées : une facture saine de 10 × 120 000 Ar + 20 % de taxes. */
public final class InvoiceFixtures {

    public static final LocalDate TODAY = LocalDate.of(2026, 11, 22);
    public static final String ACCOUNT = "MG46 0000 5000 1234 5678 9012 345";

    private InvoiceFixtures() {
    }

    public static SupplierInvoiceRequest invoice() {
        return invoice(ACCOUNT, null, "Livraison novembre");
    }

    public static SupplierInvoiceRequest invoice(String bankAccount, Long purchaseOrderId, String notes) {
        return new SupplierInvoiceRequest(1L, "FA-2026-0042", TODAY.minusDays(2), purchaseOrderId, bankAccount,
                List.of(line(1L, 10, "120000")), new BigDecimal("1200000"), new BigDecimal("240000"),
                new BigDecimal("1440000"), notes);
    }

    public static InvoiceLineRequest line(Long productId, int quantity, String unitPrice) {
        return new InvoiceLineRequest(productId, "Riz blanc 50 kg", quantity, new BigDecimal(unitPrice));
    }

    public static Supplier supplier(String bankAccount) {
        Supplier supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Agri Sud");
        supplier.setBankAccount(bankAccount);
        return supplier;
    }

    public static PurchaseOrder order(OrderStatus status, int quantity, String unitPrice) {
        PurchaseOrderLine line = new PurchaseOrderLine();
        line.setProductId(1L);
        line.setQuantity(quantity);
        line.setUnitPrice(new BigDecimal(unitPrice));
        PurchaseOrder order = new PurchaseOrder();
        order.setId(3L);
        order.setSupplierId(1L);
        order.setStatus(status);
        order.setCreatedAt(Instant.parse("2026-11-10T08:00:00Z"));
        order.getLines().add(line);
        return order;
    }

    public static InvoiceCheckContext context(SupplierInvoiceRequest invoice) {
        return new InvoiceCheckContext(invoice, Optional.of(supplier(ACCOUNT)), Optional.empty(), TODAY);
    }

    public static InvoiceCheckContext context(SupplierInvoiceRequest invoice, PurchaseOrder order) {
        return new InvoiceCheckContext(invoice, Optional.of(supplier(ACCOUNT)), Optional.of(order), TODAY);
    }
}
