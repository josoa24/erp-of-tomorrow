package com.hackaton.stock.invoice.shield;

public enum AlertSeverity {
    /** Empêche l'approbation normale : seul un ADMIN peut forcer, avec justification. */
    BLOCKING,
    /** Point à vérifier, n'empêche pas l'approbation. */
    WARNING
}
