package com.hackaton.module.common.exception;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class DomainExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
        return ErrorResponses.of(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiError> handleInvalidInput(InvalidInputException ex) {
        return ErrorResponses.of(HttpStatus.BAD_REQUEST, "VALIDATION", ex.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError> handleForbidden(ForbiddenException ex) {
        return ErrorResponses.of(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler(ImmutableResourceException.class)
    public ResponseEntity<ApiError> handleImmutable(ImmutableResourceException ex) {
        return ErrorResponses.of(HttpStatus.CONFLICT, "IMMUTABLE", ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        return ErrorResponses.of(HttpStatus.CONFLICT, "BUSINESS", ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex) {
        return ErrorResponses.of(HttpStatus.CONFLICT, "BUSINESS", "Cette donnée existe déjà ou est encore utilisée");
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex) {
        return ErrorResponses.of(HttpStatus.UNPROCESSABLE_ENTITY, "BUSINESS", ex.getMessage());
    }

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleUnavailable(ServiceUnavailableException ex) {
        return ErrorResponses.of(HttpStatus.SERVICE_UNAVAILABLE, "SERVER", ex.getMessage());
    }
}
