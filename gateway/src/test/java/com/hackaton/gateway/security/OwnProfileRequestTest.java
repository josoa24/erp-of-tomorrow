package com.hackaton.gateway.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

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

class OwnProfileRequestTest {

    private static final String SECRET = "test-secret-0123456789abcdef-0123456789abcdef";
    private static final String PATH = "/api/auth/users/me";

    private final GatewayAuthFilter filter =
            new GatewayAuthFilter(new JwtTokenParser(SECRET), new RoleAccessPolicy(), new ErrorResponseWriter());

    @Test
    void shouldForwardWithCallerIdentity_whenVenteReadsOwnProfile() {
        MockServerWebExchange exchange = get(tokenFor("VENTE"));

        ServerWebExchange forwarded = run(exchange);

        assertNotNull(forwarded);
        assertEquals("12", forwarded.getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ID));
        assertEquals("VENTE", forwarded.getRequest().getHeaders().getFirst(GatewayAuthFilter.HEADER_USER_ROLE));
    }

    @Test
    void shouldReturn401_whenOwnProfileIsReadWithoutToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get(PATH).build());

        ServerWebExchange forwarded = run(exchange);

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        assertNull(forwarded);
    }

    private MockServerWebExchange get(String token) {
        return MockServerWebExchange.from(MockServerHttpRequest.get(PATH).header("Authorization", "Bearer " + token).build());
    }

    private String tokenFor(String role) {
        return Jwts.builder()
                .subject("toky@boutique-tana.mg")
                .claim("userId", 12)
                .claim("role", role)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private ServerWebExchange run(MockServerWebExchange exchange) {
        AtomicReference<ServerWebExchange> forwarded = new AtomicReference<>();
        filter.filter(exchange, next -> {
            forwarded.set(next);
            return Mono.empty();
        }).block();
        return forwarded.get();
    }
}
