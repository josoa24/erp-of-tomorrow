package com.hackaton.gateway.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class ErrorResponseWriter {

    public Mono<Void> write(ServerWebExchange exchange, HttpStatus status, String type, String message) {
        String body = String.format(
                "{\"status\":%d,\"type\":\"%s\",\"message\":\"%s\",\"timestamp\":\"%s\"}",
                status.value(), type, message, Instant.now());
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}
