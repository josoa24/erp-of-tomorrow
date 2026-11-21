package com.hackaton.gateway.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GatewayAuthFilter implements GlobalFilter, Ordered {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_EMAIL = "X-User-Email";
    public static final String HEADER_USER_ROLE = "X-User-Role";
    private static final String BEARER = "Bearer ";

    private final JwtTokenParser tokenParser;
    private final RoleAccessPolicy accessPolicy;
    private final ErrorResponseWriter errorWriter;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        if (HttpMethod.OPTIONS.equals(request.getMethod()) || accessPolicy.isPublic(path)) {
            return chain.filter(exchange);
        }
        Optional<AuthenticatedUser> user = extractUser(request);
        if (user.isEmpty()) {
            return errorWriter.write(exchange, HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "Authentification requise");
        }
        if (!accessPolicy.isAllowed(path, user.get().role())) {
            return errorWriter.write(exchange, HttpStatus.FORBIDDEN, "FORBIDDEN",
                    "Le rôle " + user.get().role() + " n'a pas accès à ce module");
        }
        return chain.filter(exchange.mutate().request(withIdentity(request, user.get())).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private Optional<AuthenticatedUser> extractUser(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(BEARER)) {
            return Optional.empty();
        }
        return tokenParser.parse(header.substring(BEARER.length()));
    }

    private ServerHttpRequest withIdentity(ServerHttpRequest request, AuthenticatedUser user) {
        return request.mutate().headers(headers -> {
            headers.remove(HEADER_USER_ID);
            headers.remove(HEADER_USER_EMAIL);
            headers.remove(HEADER_USER_ROLE);
            headers.add(HEADER_USER_ID, String.valueOf(user.userId()));
            headers.add(HEADER_USER_EMAIL, user.email());
            headers.add(HEADER_USER_ROLE, user.role());
        }).build();
    }
}
