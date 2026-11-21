package com.hackaton.stock.invoice.service.decision;

import com.hackaton.stock.common.security.Actor;
import com.hackaton.stock.invoice.dto.SupplierInvoiceResponse;

public interface InvoiceDecider {

    /** Approuve pour paiement. Une facture BLOCKED exige un ADMIN, autre que son auteur, et une justification. */
    SupplierInvoiceResponse approve(Long id, String justification, Actor actor);

    SupplierInvoiceResponse reject(Long id, String reason, Actor actor);
}
