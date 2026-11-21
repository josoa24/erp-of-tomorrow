package com.hackaton.stock.invoice.repository;

import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierInvoiceRepository extends JpaRepository<SupplierInvoice, Long> {

    boolean existsBySupplierIdAndInvoiceNumberKeyAndStatusNot(Long supplierId, String key, InvoiceStatus excluded);

    boolean existsBySupplierIdAndTotalAmountAndInvoiceDateAndStatusNot(
            Long supplierId, BigDecimal totalAmount, LocalDate invoiceDate, InvoiceStatus excluded);

    List<SupplierInvoice> findBySupplierIdAndStatusNot(Long supplierId, InvoiceStatus excluded);

    List<SupplierInvoice> findByPurchaseOrderIdAndStatusNot(Long purchaseOrderId, InvoiceStatus excluded);

    List<SupplierInvoice> findAllByOrderByCreatedAtDesc();
}
