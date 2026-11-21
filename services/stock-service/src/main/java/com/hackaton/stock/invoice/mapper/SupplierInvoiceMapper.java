package com.hackaton.stock.invoice.mapper;

import com.hackaton.stock.common.util.BankAccounts;
import com.hackaton.stock.invoice.dto.InvoiceLineResponse;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceAlert;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.entity.SupplierInvoiceLine;
import com.hackaton.stock.invoice.shield.InvoiceNumbers;
import com.hackaton.stock.invoice.shield.ShieldVerdict;
import org.springframework.stereotype.Component;

@Component
public class SupplierInvoiceMapper {

    public SupplierInvoice toEntity(SupplierInvoiceRequest request, ShieldReport report, Long createdBy) {
        SupplierInvoice invoice = new SupplierInvoice();
        invoice.setSupplierId(request.supplierId());
        invoice.setInvoiceNumber(request.invoiceNumber().trim());
        invoice.setInvoiceNumberKey(InvoiceNumbers.key(request.invoiceNumber()));
        invoice.setInvoiceDate(request.invoiceDate());
        invoice.setPurchaseOrderId(request.purchaseOrderId());
        invoice.setBankAccount(BankAccounts.clean(request.bankAccount()));
        invoice.setSubtotal(request.subtotal());
        invoice.setTaxAmount(request.taxAmount());
        invoice.setTotalAmount(request.totalAmount());
        invoice.setNotes(request.notes());
        invoice.setVerdict(report.verdict());
        invoice.setStatus(report.verdict() == ShieldVerdict.BLOCKED ? InvoiceStatus.BLOCKED : InvoiceStatus.CLEARED);
        invoice.setCreatedBy(createdBy);
        request.lines().forEach(line -> {
            SupplierInvoiceLine entityLine = new SupplierInvoiceLine();
            entityLine.setProductId(line.productId());
            entityLine.setDescription(line.description().trim());
            entityLine.setQuantity(line.quantity());
            entityLine.setUnitPrice(line.unitPrice());
            invoice.getLines().add(entityLine);
        });
        report.alerts().forEach(alert ->
                invoice.getAlerts().add(new InvoiceAlert(alert.code(), alert.severity(), alert.message())));
        return invoice;
    }

    public SupplierInvoiceResponse toResponse(SupplierInvoice invoice) {
        return new SupplierInvoiceResponse(
                invoice.getId(),
                invoice.getSupplierId(),
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getPurchaseOrderId(),
                BankAccounts.last4(invoice.getBankAccount()),
                invoice.getLines().stream().map(line -> new InvoiceLineResponse(
                        line.getProductId(), line.getDescription(), line.getQuantity(), line.getUnitPrice())).toList(),
                invoice.getSubtotal(),
                invoice.getTaxAmount(),
                invoice.getTotalAmount(),
                invoice.getStatus(),
                invoice.getVerdict(),
                invoice.getAlerts().stream()
                        .map(alert -> new ShieldAlert(alert.getCode(), alert.getSeverity(), alert.getMessage()))
                        .toList(),
                invoice.getCreatedBy(),
                invoice.getCreatedAt(),
                invoice.getDecidedBy(),
                invoice.getDecidedAt(),
                invoice.getDecisionNote());
    }
}
