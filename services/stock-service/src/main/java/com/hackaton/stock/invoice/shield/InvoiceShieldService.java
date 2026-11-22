package com.hackaton.stock.invoice.shield;

import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.purchaseorder.entity.PurchaseOrder;
import com.hackaton.stock.purchaseorder.repository.PurchaseOrderRepository;
import com.hackaton.stock.supplier.repository.SupplierRepository;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Charge le contexte une fois, exécute tous les contrôles et en déduit le verdict. */
@Service
@Transactional(readOnly = true)
public class InvoiceShieldService implements InvoiceShield {

    private final List<InvoiceCheck> checks;
    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository orderRepository;
    private final Clock clock;

    @Autowired
    public InvoiceShieldService(List<InvoiceCheck> checks, SupplierRepository supplierRepository,
                                PurchaseOrderRepository orderRepository) {
        this(checks, supplierRepository, orderRepository, Clock.systemDefaultZone());
    }

    InvoiceShieldService(List<InvoiceCheck> checks, SupplierRepository supplierRepository,
                         PurchaseOrderRepository orderRepository, Clock clock) {
        this.checks = checks;
        this.supplierRepository = supplierRepository;
        this.orderRepository = orderRepository;
        this.clock = clock;
    }

    @Override
    public ShieldReport inspect(SupplierInvoiceRequest invoice) {
        Optional<PurchaseOrder> order = invoice.purchaseOrderId() == null
                ? Optional.empty()
                : orderRepository.findById(invoice.purchaseOrderId());
        InvoiceCheckContext context = new InvoiceCheckContext(
                invoice, supplierRepository.findById(invoice.supplierId()), order, LocalDate.now(clock));
        List<ShieldAlert> alerts = checks.stream()
                .flatMap(check -> check.check(context).stream())
                .toList();
        return ShieldReport.of(alerts);
    }
}
