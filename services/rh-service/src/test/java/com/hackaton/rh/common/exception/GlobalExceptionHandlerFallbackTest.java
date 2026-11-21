package com.hackaton.rh.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

class GlobalExceptionHandlerFallbackTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturn400Validation_whenParameterIsMissing() {
        assertError(handler.handleMissingParameter(new MissingServletRequestParameterException("from", "LocalDate")),
                400, "VALIDATION");
    }

    @Test
    void shouldReturn405_whenMethodIsNotSupported() {
        assertError(handler.handleMethodNotAllowed(new HttpRequestMethodNotSupportedException("PATCH")),
                405, "VALIDATION");
    }

    @Test
    void shouldReturn415_whenBodyIsNotJson() {
        assertError(handler.handleMediaType(new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, List.of())),
                415, "VALIDATION");
    }

    @Test
    void shouldReturn404NotFound_whenPathIsUnknown() {
        assertError(handler.handleUnknownPath(new NoResourceFoundException(HttpMethod.GET, "inconnu")),
                404, "NOT_FOUND");
    }

    @Test
    void shouldReturn500Server_withoutLeakingDetails_whenErrorIsUnexpected() {
        ResponseEntity<ApiError> response = handler.handleUnexpected(new IllegalStateException("secret interne"));

        assertError(response, 500, "SERVER");
        assertEquals("Erreur interne du service", response.getBody().message());
    }

    private void assertError(ResponseEntity<ApiError> response, int status, String type) {
        assertEquals(status, response.getStatusCode().value());
        assertEquals(status, response.getBody().status());
        assertEquals(type, response.getBody().type());
    }
}
