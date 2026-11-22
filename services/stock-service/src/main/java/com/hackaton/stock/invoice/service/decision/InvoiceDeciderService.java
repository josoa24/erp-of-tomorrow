package com.hackaton.stock.invoice.service.decision;

import com.hackaton.stock.common.exception.ForbiddenException;
import com.hackaton.stock.common.exception.ImmutableResourceException;
import com.hackaton.stock.common.exception.InvalidRequestException;
import com.hackaton.stock.common.exception.ResourceNotFoundException;
import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.entity.InvoiceStatus;
import com.hackaton.stock.invoice.entity.SupplierInvoice;
import com.hackaton.stock.invoice.mapper.SupplierInvoiceMapper;
import com.hackaton.stock.invoice.repository.SupplierInvoiceRepository;
import com.hackaton.stock.invoice.service.audit.InvoiceAuditTrail;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Une décision est définitive : une facture approuvée ou rejetée ne change plus (409 IMMUTABLE). */
@Service
@RequiredArgsConstructor
public class InvoiceDeciderService implements InvoiceDecider {

    private final SupplierInvoiceRepository invoiceRepository;
    private final SupplierInvoiceMapper invoiceMapper;
    private final InvoiceAuditTrail auditTrail;

    @Override
    @Transactional
    public SupplierInvoiceResponse approve(Long id, String justification, Actor actor) {
        SupplierInvoice invoice = findPending(id);
        InvoiceEventType eventType = InvoiceEventType.APPROVED;
        if (invoice.getStatus() == InvoiceStatus.BLOCKED) {
            checkOverride(invoice, justification, actor);
            eventType = InvoiceEventType.OVERRIDDEN;
        }
        decide(invoice, InvoiceStatus.APPROVED, justification, actor);
        auditTrail.record(id, eventType, actor, justification);
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    @Transactional
    public SupplierInvoiceResponse reject(Long id, String reason, Actor actor) {
        SupplierInvoice invoice = findPending(id);
        decide(invoice, InvoiceStatus.REJECTED, reason, actor);
        auditTrail.record(id, InvoiceEventType.REJECTED, actor, reason);
        return invoiceMapper.toResponse(invoice);
    }

    private SupplierInvoice findPending(Long id) {
        SupplierInvoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture fournisseur", id));
        if (invoice.getStatus() == InvoiceStatus.APPROVED || invoice.getStatus() == InvoiceStatus.REJECTED) {
            throw new ImmutableResourceException("La facture " + id + " a déjà été traitée (" + invoice.getStatus() + ")");
        }
        return invoice;
    }

    private static void checkOverride(SupplierInvoice invoice, String justification, Actor actor) {
        if (!actor.isAdmin()) {
            throw new ForbiddenException("Facture bloquée par Invoice Shield : seul un ADMIN peut forcer l'approbation");
        }
        if (actor.isSameUser(invoice.getCreatedBy())) {
            throw new ForbiddenException("Contrôle à quatre yeux : l'auteur de la facture ne peut pas forcer son approbation");
        }
        if (justification == null || justification.isBlank()) {
            throw new InvalidRequestException("Une justification est obligatoire pour approuver une facture bloquée");
        }
    }

    private void decide(SupplierInvoice invoice, InvoiceStatus status, String note, Actor actor) {
        invoice.setStatus(status);
        invoice.setDecidedBy(actor.id());
        invoice.setDecidedAt(Instant.now());
        invoice.setDecisionNote(note);
        invoiceRepository.save(invoice);
    }
}
