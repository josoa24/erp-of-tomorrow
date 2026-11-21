package com.hackaton.auth.support;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class GatewayHeaders {

    private GatewayHeaders() {
    }

    public static MockHttpServletRequestBuilder withIdentity(MockHttpServletRequestBuilder request) {
        return request
                .header("X-User-Id", "1")
                .header("X-User-Email", "admin@erp.local")
                .header("X-User-Role", "ADMIN");
    }
}
