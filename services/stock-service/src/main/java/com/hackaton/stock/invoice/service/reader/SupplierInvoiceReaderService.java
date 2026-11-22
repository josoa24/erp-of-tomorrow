package com.hackaton.stock.invoice.service.reader;

import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.invoice.dto.InvoiceEventResponse;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.mapper.SupplierInvoiceMapper;
import com.hackaton.stock.invoice.repository.InvoiceEventRepository;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierInvoiceReaderService implements SupplierInvoiceReader {

    private final SupplierInvoiceRepository invoiceRepository;
    private final InvoiceEventRepository eventRepository;
    private final SupplierInvoiceMapper invoiceMapper;

    @Override
    public List<SupplierInvoiceResponse> search(InvoiceStatus status, Long supplierId) {
        return invoiceRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(invoice -> status == null || invoice.getStatus() == status)
                .filter(invoice -> supplierId == null || invoice.getSupplierId().equals(supplierId))
                .map(invoiceMapper::toResponse)
                .toList();
    }

    @Override
    public SupplierInvoiceResponse findById(Long id) {
        return invoiceRepository.findById(id)
                .map(invoiceMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Facture fournisseur", id));
    }

    @Override
    public List<InvoiceEventResponse> history(Long id) {
        if (!invoiceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Facture fournisseur", id);
        }
        return eventRepository.findByInvoiceIdOrderByIdAsc(id).stream()
                .map(event -> new InvoiceEventResponse(event.getType(), event.getActorId(), event.getActorEmail(),
                        event.getActorRole(), event.getDetail(), event.getCreatedAt()))
                .toList();
    }
}
