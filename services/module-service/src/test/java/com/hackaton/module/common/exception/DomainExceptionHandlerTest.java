package com.hackaton.module.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

class DomainExceptionHandlerTest {

    private final DomainExceptionHandler handler = new DomainExceptionHandler();

    @Test
    void shouldReturn404NotFound_whenResourceIsMissing() {
        assertError(handler.handleNotFound(new ResourceNotFoundException("Module introuvable")), 404, "NOT_FOUND");
    }

    @Test
    void shouldReturn400Validation_whenInputIsInvalid() {
        assertError(handler.handleInvalidInput(new InvalidInputException("Champ obligatoire")), 400, "VALIDATION");
    }

    @Test
    void shouldReturn403Forbidden_whenRoleIsNotAllowed() {
        assertError(handler.handleForbidden(new ForbiddenException("Réservé")), 403, "FORBIDDEN");
    }

    @Test
    void shouldReturn409Immutable_whenRecordIsFinal() {
        assertError(handler.handleImmutable(new ImmutableResourceException("Final")), 409, "IMMUTABLE");
    }

    @Test
    void shouldReturn409Business_whenDataAlreadyExists() {
        assertError(handler.handleConflict(new ConflictException("Existe")), 409, "BUSINESS");
        assertError(handler.handleIntegrity(new DataIntegrityViolationException("uk")), 409, "BUSINESS");
    }

    @Test
    void shouldReturn422Business_whenBusinessRuleFails() {
        assertError(handler.handleBusiness(new BusinessException("Référence introuvable")), 422, "BUSINESS");
    }

    @Test
    void shouldReturn503Server_whenRemoteServiceIsDown() {
        assertError(handler.handleUnavailable(new ServiceUnavailableException("Vente indisponible")), 503, "SERVER");
    }

    private void assertError(ResponseEntity<ApiError> response, int status, String type) {
        assertEquals(status, response.getStatusCode().value());
        assertEquals(status, response.getBody().status());
        assertEquals(type, response.getBody().type());
    }
}
