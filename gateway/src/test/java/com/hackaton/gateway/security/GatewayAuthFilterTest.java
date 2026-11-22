package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

class GatewayAuthFilterTest {

    private static final String SECRET = "test-secret-0123456789abcdef-0123456789abcdef";

    private final GatewayAuthFilter filter =
            new GatewayAuthFilter(new JwtTokenParser(SECRET), new RoleAccessPolicy(), new ErrorResponseWriter());

    private String tokenFor(String role, String secret, long ttlMillis) {
        return Jwts.builder()
                .subject(role.toLowerCase() + "@erp.local")
                .claim("userId", 7)
                .claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + ttlMillis))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private String tokenWithRoles(String role, java.util.List<String> roles) {
        return Jwts.builder()
                .subject("multi@erp.local")
                .claim("userId", 7)
                .claim("role", role)
                .claim("roles", roles)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private MockServerWebExchange exchange(String path, String token) {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get(path);
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return MockServerWebExchange.from(builder.build());
    }

    private AtomicReference<ServerWebExchange> run(MockServerWebExchange exchange) {
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        filter.filter(exchange, next -> {
            forwarded.set(next);
            return Mono.empty();
        }).block();
        return forwarded;
    }

    @Test
    void shouldReturn401_whenTokenIsMissing() {
        MockServerWebExchange exchange = exchange("/api/vente/sales", null);

        AtomicReference<ServerWebExchange> forwarded = run(exchange);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertNull(forwarded.get());
    }

    @Test
    void shouldReturn401_whenTokenSignatureIsInvalid() {
        String forged = tokenFor("ADMIN", "another-secret-0123456789abcdef-0123456789", 60_000);
        MockServerWebExchange exchange = exchange("/api/stock/products", forged);

        run(exchange);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturn401_whenTokenIsExpired() {
        MockServerWebExchange exchange = exchange("/api/rh/employees", tokenFor("RH", SECRET, -1_000));

        run(exchange);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReturn403_whenRhAccessesVenteModule() {
        MockServerWebExchange exchange = exchange("/api/vente/sales", tokenFor("RH", SECRET, 60_000));

        AtomicReference<ServerWebExchange> forwarded = run(exchange);

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
        assertNull(forwarded.get());
    }

    @Test
    void shouldReturn403_whenVenteAccessesStockModule() {
        MockServerWebExchange exchange = exchange("/api/stock/products", tokenFor("VENTE", SECRET, 60_000));

        run(exchange);

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldForwardWithIdentityHeaders_whenRoleMatchesModule() {
        MockServerWebExchange exchange = exchange("/api/rh/employees", tokenFor("RH", SECRET, 60_000));

        AtomicReference<ServerWebExchange> forwarded = run(exchange);

        assertTrue(forwarded.get() != null);
        assertEquals("RH", forwarded.get().getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ROLE));
        assertEquals("7", forwarded.get().getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ID));
    }

    @Test
    void shouldLetLoginThroughWithoutToken() {
        MockServerWebExchange exchange = exchange("/api/auth/login", null);

        AtomicReference<ServerWebExchange> forwarded = run(exchange);

        assertTrue(forwarded.get() != null);
    }

    @Test
    void shouldAllowEachModule_whenAnyRoleGrantsIt() {
        String token = tokenWithRoles("VENTE", java.util.List.of("VENTE", "STOCK"));

        AtomicReference<ServerWebExchange> stock = run(exchange("/api/stock/products", token));
        MockServerWebExchange rh = exchange("/api/rh/employees", token);
        run(rh);

        assertTrue(stock.get() != null);
        assertEquals("STOCK", stock.get().getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ROLE));
        assertEquals("STOCK,VENTE", stock.get().getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ROLES));
        assertEquals(HttpStatus.FORBIDDEN, rh.getResponse().getStatusCode());
    }

    @Test
    void shouldIgnoreClientSentRolesHeaders() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/auth/users")
                .header("Authorization", "Bearer " + tokenFor("STOCK", SECRET, 60_000))
                .header(GatewayAuthFilter.HEADER_USER_ROLES, "ADMIN")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        run(exchange);

        assertEquals(HttpStatus.FORBIDDEN, exchange.getResponse().getStatusCode());
    }

    @Test
    void shouldReplaceClientSentRolesHeader_whenRequestIsAllowed() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/stock/products")
                .header("Authorization", "Bearer " + tokenFor("STOCK", SECRET, 60_000))
                .header(GatewayAuthFilter.HEADER_USER_ROLES, "ADMIN")
                .build();

        AtomicReference<ServerWebExchange> forwarded = run(MockServerWebExchange.from(request));

        assertEquals(java.util.List.of("STOCK"),
                forwarded.get().getRequest().getHeaders().get(GatewayAuthFilter.HEADER_USER_ROLES));
    }
}
