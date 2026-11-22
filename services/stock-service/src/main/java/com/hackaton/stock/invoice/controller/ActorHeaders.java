package com.hackaton.stock.invoice.controller;

/** En-têtes d'identité posés par la gateway après vérification du token. */
final class ActorHeaders {

    static final String ID = "X-User-Id";
    static final String EMAIL = "X-User-Email";
    static final String ROLE = "X-User-Role";

    private ActorHeaders() {
    }
}
