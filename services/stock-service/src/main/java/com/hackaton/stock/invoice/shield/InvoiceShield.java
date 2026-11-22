package com.hackaton.stock.invoice.shield;

import com.hackaton.stock.invoice.dto.ShieldReport;
import com.hackaton.stock.invoice.dto.SupplierInvoiceRequest;

/** Contrôle anti-fraude d'une facture fournisseur, sans rien enregistrer. */
public interface InvoiceShield {

    ShieldReport inspect(SupplierInvoiceRequest invoice);
}
