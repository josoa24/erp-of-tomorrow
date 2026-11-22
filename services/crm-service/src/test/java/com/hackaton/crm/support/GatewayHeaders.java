package com.hackaton.crm.support;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class GatewayHeaders {

    public static final String USER_EMAIL = "admin@erp.local";

    private GatewayHeaders() {
    }

    public static MockHttpServletRequestBuilder withIdentity(MockHttpServletRequestBuilder request) {
        return request
                .header("X-User-Id", "1")
                .header("X-User-Email", USER_EMAIL)
                .header("X-User-Role", "ADMIN");
    }
}
