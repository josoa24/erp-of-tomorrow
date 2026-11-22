package com.hackaton.stock.invoice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record SupplierInvoiceRequest(
        @NotNull Long supplierId,
        @NotBlank @Size(max = 64) String invoiceNumber,
        @NotNull LocalDate invoiceDate,
        Long purchaseOrderId,
        @Size(max = 64) String bankAccount,
        @NotEmpty @Size(max = 200) List<@Valid @NotNull InvoiceLineRequest> lines,
        @DecimalMin("0.0") BigDecimal subtotal,
        @DecimalMin("0.0") BigDecimal taxAmount,
        @NotNull @DecimalMin("0.0") BigDecimal totalAmount,
        @Size(max = 4000) String notes) {
}
