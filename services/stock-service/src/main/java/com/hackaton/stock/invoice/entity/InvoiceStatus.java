package com.hackaton.stock.invoice.entity;

public enum InvoiceStatus {
    /** Contrôlée sans alerte bloquante, en attente de décision. */
    CLEARED,
    /** Au moins une alerte bloquante : seul un ADMIN peut forcer l'approbation, avec justification. */
    BLOCKED,
    /** Approuvée pour paiement : immuable. */
    APPROVED,
    /** Rejetée : immuable. */
    REJECTED
}
