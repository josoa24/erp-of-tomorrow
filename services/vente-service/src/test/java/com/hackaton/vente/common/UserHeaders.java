package com.hackaton.vente.common;

import org.springframework.test.web.servlet.request.RequestPostProcessor;

public final class UserHeaders {

    private UserHeaders() {
    }

    public static RequestPostProcessor asAdmin() {
        return request -> {
            request.addHeader("X-User-Id", "1");
            request.addHeader("X-User-Email", "admin@erp.local");
            request.addHeader("X-User-Role", "ADMIN");
            return request;
        };
    }
}
