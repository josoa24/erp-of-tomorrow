package com.hackaton.module.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class IdentityForwardingTest {

    private final IdentityForwarding forwarding = new IdentityForwarding();
    private final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

    @AfterEach
    void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void shouldCopyIdentityHeaders_fromIncomingRequest() throws Exception {
        MockHttpServletRequest incoming = new MockHttpServletRequest();
        incoming.addHeader("X-User-Id", "3");
        incoming.addHeader("X-User-Email", "user@erp.local");
        incoming.addHeader("X-User-Role", "ADMIN");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(incoming));
        MockClientHttpRequest outgoing = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://service/x"));

        forwarding.intercept(outgoing, new byte[0], execution);

        assertEquals("3", outgoing.getHeaders().getFirst("X-User-Id"));
        assertEquals("user@erp.local", outgoing.getHeaders().getFirst("X-User-Email"));
        assertEquals("ADMIN", outgoing.getHeaders().getFirst("X-User-Role"));
        verify(execution).execute(any(), any());
    }

    @Test
    void shouldAddNothing_whenCalledOutsideAnHttpRequest() throws Exception {
        MockClientHttpRequest outgoing = new MockClientHttpRequest(HttpMethod.GET, URI.create("http://service/x"));

        forwarding.intercept(outgoing, new byte[0], execution);

        assertFalse(outgoing.getHeaders().containsKey("X-User-Id"));
        verify(execution).execute(any(), any());
    }
}
