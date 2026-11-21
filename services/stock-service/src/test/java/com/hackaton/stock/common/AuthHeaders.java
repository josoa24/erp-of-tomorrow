package com.hackaton.stock.common;

import org.springframework.http.HttpHeaders;

/** En-têtes d'identité transmis par la gateway, envoyés sur chaque requête de test. */
public final class AuthHeaders {

    private AuthHeaders() {
    }

    public static HttpHeaders admin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Id", "1");
        headers.add("X-User-Email", "admin@erp.local");
        headers.add("X-User-Role", "ADMIN");
        return headers;
    }
}
