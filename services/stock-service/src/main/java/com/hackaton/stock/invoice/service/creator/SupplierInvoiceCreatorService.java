package com.hackaton.stock.invoice.service.creator;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.ShieldAlert;
import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.mapper.SupplierInvoiceMapper;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.service.audit.InvoiceAuditTrail;
import com.hackaton.stock.invoice.shield.InvoiceShield;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Une facture suspecte est enregistrée quand même (statut BLOCKED) : la tentative reste tracée et
 * ne peut être payée qu'après une décision explicite.
 */
@Service
@RequiredArgsConstructor
public class SupplierInvoiceCreatorService implements SupplierInvoiceCreator {

    private final InvoiceShield invoiceShield;
    private final SupplierInvoiceRepository invoiceRepository;
    private final SupplierInvoiceMapper invoiceMapper;
    private final InvoiceAuditTrail auditTrail;

    @Override
    @Transactional
    public SupplierInvoiceResponse create(SupplierInvoiceRequest request, Actor actor) {
        ShieldReport report = invoiceShield.inspect(request);
        SupplierInvoice saved = invoiceRepository.save(invoiceMapper.toEntity(request, report, actor.id()));
        auditTrail.record(saved.getId(), InvoiceEventType.CREATED, actor,
                "Facture " + saved.getInvoiceNumber() + " du fournisseur " + saved.getSupplierId());
        auditTrail.record(saved.getId(), InvoiceEventType.CHECKED, actor, describe(report));
        return invoiceMapper.toResponse(saved);
    }

    private static String describe(ShieldReport report) {
        String codes = report.alerts().stream()
                .map(ShieldAlert::code)
                .map(Enum::name)
                .distinct()
                .collect(Collectors.joining(", "));
        return "Verdict " + report.verdict() + (codes.isEmpty() ? "" : " : " + codes);
    }
}
