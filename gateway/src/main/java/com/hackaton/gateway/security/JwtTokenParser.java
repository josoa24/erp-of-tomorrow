package com.hackaton.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenParser {

    private final SecretKey key;

    public JwtTokenParser(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Optional<AuthenticatedUser> parse(String token) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(toUser(claims));
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private AuthenticatedUser toUser(Claims claims) {
        Number userId = claims.get("userId", Number.class);
        String role = claims.get("role", String.class);
        if (role == null || userId == null) {
            throw new IllegalArgumentException("Claims manquants");
        }
        return new AuthenticatedUser(claims.getSubject(), userId.longValue(), rolesOf(claims.get("roles"), role));
    }

    private List<String> rolesOf(Object claim, String primary) {
        List<String> roles = claim instanceof Collection<?> values
                ? values.stream().filter(String.class::isInstance).map(String.class::cast).distinct().toList()
                : List.of();
        if (roles.isEmpty()) {
            return List.of(primary);
        }
        return roles.stream().sorted(Comparator.comparingInt(RolePriority::rank)).toList();
    }
}
