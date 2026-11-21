package com.hackaton.rh.common;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

/**
 * Requêtes de test portant les en-têtes d'identité qu'ajoute la gateway.
 */
public final class GatewayRequests {

    private GatewayRequests() {
    }

    public static MockHttpServletRequestBuilder get(String url, Object... vars) {
        return withIdentity(MockMvcRequestBuilders.get(url, vars));
    }

    public static MockHttpServletRequestBuilder delete(String url, Object... vars) {
        return withIdentity(MockMvcRequestBuilders.delete(url, vars));
    }

    public static MockHttpServletRequestBuilder post(String url, Object... vars) {
        return withIdentity(MockMvcRequestBuilders.post(url, vars));
    }

    public static MockHttpServletRequestBuilder postJson(String url, String body) {
        return post(url).contentType(MediaType.APPLICATION_JSON).content(body);
    }

    public static MockHttpServletRequestBuilder putJson(String url, String body, Object... vars) {
        return withIdentity(MockMvcRequestBuilders.put(url, vars))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body);
    }

    private static MockHttpServletRequestBuilder withIdentity(MockHttpServletRequestBuilder builder) {
        return builder
                .header("X-User-Id", "1")
                .header("X-User-Email", "admin@erp.local")
                .header("X-User-Role", "ADMIN");
    }
}
