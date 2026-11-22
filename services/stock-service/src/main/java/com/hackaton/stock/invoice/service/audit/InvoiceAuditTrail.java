package com.hackaton.stock.invoice.service.audit;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.entity.InvoiceEvent;
import com.hackaton.stock.invoice.entity.InvoiceEventType;
import com.hackaton.stock.invoice.repository.InvoiceEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Ajoute une ligne au journal d'audit d'une facture (jamais de coordonnées bancaires dans le détail). */
@Component
@RequiredArgsConstructor
public class InvoiceAuditTrail {

    private final InvoiceEventRepository eventRepository;

    public void record(Long invoiceId, InvoiceEventType type, Actor actor, String detail) {
        InvoiceEvent event = new InvoiceEvent();
        event.setInvoiceId(invoiceId);
        event.setType(type);
        event.setActorId(actor.id());
        event.setActorEmail(actor.email());
        event.setActorRole(actor.role());
        event.setDetail(detail);
        eventRepository.save(event);
    }
}
