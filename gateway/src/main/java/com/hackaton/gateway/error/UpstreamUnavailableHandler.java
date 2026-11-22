package com.hackaton.gateway.error;

import com.hackaton.gateway.security.ErrorResponseWriter;
import java.net.ConnectException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
@RequiredArgsConstructor
public class UpstreamUnavailableHandler implements ErrorWebExceptionHandler {

    private static final String TYPE = "SERVICE_UNAVAILABLE";
    private static final String MESSAGE = "Le service demandé est indisponible";

    private final ErrorResponseWriter errorResponseWriter;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable error) {
        if (!isConnectionFailure(error)) {
            return Mono.error(error);
        }
        return errorResponseWriter.write(exchange, HttpStatus.SERVICE_UNAVAILABLE, TYPE, MESSAGE);
    }

    private static boolean isConnectionFailure(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConnectException) {
                return true;
            }
        }
        return false;
    }
}
