package com.hackaton.rh.common.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class GatewayIdentityFilterTest {

    private final GatewayIdentityFilter filter =
            new GatewayIdentityFilter(new ObjectMapper().registerModule(new JavaTimeModule()));

    @Test
    void shouldRejectWithUnauthorized_whenIdentityHeadersAreMissing() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        MockHttpServletResponse response = run(new MockHttpServletRequest("GET", "/employees"), chain);

        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"type\":\"UNAUTHORIZED\""));
        assertNull(chain.getRequest());
    }

    @Test
    void shouldReject_whenOnlyUserIdIsPresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/employees");
        request.addHeader(GatewayIdentityFilter.USER_ID, "1");

        assertEquals(401, run(request, new MockFilterChain()).getStatus());
    }

    @Test
    void shouldPass_whenGatewayHeadersArePresent() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/employees");
        request.addHeader(GatewayIdentityFilter.USER_ID, "1");
        request.addHeader(GatewayIdentityFilter.USER_ROLE, "ADMIN");
        MockFilterChain chain = new MockFilterChain();

        assertEquals(200, run(request, chain).getStatus());
        assertNotNull(chain.getRequest());
    }

    @Test
    void shouldLeaveOpenPathsReachable_withoutHeaders() throws Exception {
        for (String path : new String[] {"/v3/api-docs", "/v3/api-docs/swagger-config", "/swagger-ui.html", "/swagger-ui/index.html", "/actuator/health"}) {
            MockFilterChain chain = new MockFilterChain();
            run(new MockHttpServletRequest("GET", path), chain);
            assertNotNull(chain.getRequest(), path);
        }
    }

    @Test
    void shouldNotTreatSimilarPathAsOpen() throws Exception {
        MockFilterChain chain = new MockFilterChain();
        run(new MockHttpServletRequest("GET", "/v4/api-docs"), chain);
        assertNull(chain.getRequest());
    }

    private MockHttpServletResponse run(MockHttpServletRequest request, MockFilterChain chain) throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(request, response, chain);
        return response;
    }
}
