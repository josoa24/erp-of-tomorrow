package com.hackaton.stock.invoice.service.creator;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;

public interface SupplierInvoiceCreator {

    /** Contrôle puis enregistre la facture : statut CLEARED, ou BLOCKED si une alerte est bloquante. */
    SupplierInvoiceResponse create(SupplierInvoiceRequest request, Actor actor);
}
