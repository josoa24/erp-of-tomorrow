package com.hackaton.stock.invoice.service.reader;

import com.hackaton.stock.invoice.dto.InvoiceEventResponse;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import java.util.List;

public interface SupplierInvoiceReader {

    List<SupplierInvoiceResponse> search(InvoiceStatus status, Long supplierId);

    SupplierInvoiceResponse findById(Long id);

    List<InvoiceEventResponse> history(Long id);
}
