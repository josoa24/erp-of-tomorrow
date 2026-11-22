package com.hackaton.module.support;

import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public final class GatewayHeaders {

    public static final String ADMIN_EMAIL = "hery.rabe@nova.mg";
    public static final String SELLER_EMAIL = "rasoa.andria@nova.mg";

    private GatewayHeaders() {
    }

    public static MockHttpServletRequestBuilder asAdmin(MockHttpServletRequestBuilder request) {
        return as(request, "1", ADMIN_EMAIL, "ADMIN");
    }

    public static MockHttpServletRequestBuilder asSeller(MockHttpServletRequestBuilder request) {
        return as(request, "2", SELLER_EMAIL, "VENTE");
    }

    private static MockHttpServletRequestBuilder as(MockHttpServletRequestBuilder request, String id, String email,
            String role) {
        return request.header("X-User-Id", id).header("X-User-Email", email).header("X-User-Role", role);
    }
}
