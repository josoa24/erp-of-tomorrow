package com.hackaton.vente.common.security;

import java.io.IOException;
import java.util.List;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class IdentityForwarding implements ClientHttpRequestInterceptor, RestClientCustomizer {

    static final List<String> HEADERS = List.of("X-User-Id", "X-User-Email", "X-User-Role", "X-User-Roles");

    @Override
    public void customize(RestClient.Builder builder) {
        builder.requestInterceptor(this);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes) {
            HEADERS.forEach(name -> {
                String value = attributes.getRequest().getHeader(name);
                if (value != null) {
                    request.getHeaders().set(name, value);
                }
            });
        }
        return execution.execute(request, body);
    }
}
