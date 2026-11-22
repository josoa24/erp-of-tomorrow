package com.hackaton.stock.invoice.shield;

import java.util.Locale;

/** Clé de comparaison d'un numéro de facture : « fa-2026 / 0042 » et « FA20260042 » sont identiques. */
public final class InvoiceNumbers {

    private InvoiceNumbers() {
    }

    public static String key(String invoiceNumber) {
        return invoiceNumber.replaceAll("[^A-Za-z0-9]", "").toUpperCase(Locale.ROOT);
    }
}
