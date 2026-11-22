package com.hackaton.module.common.exception;

import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@Order(2)
@RestControllerAdvice
public class RequestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ErrorResponses.of(HttpStatus.BAD_REQUEST, "VALIDATION", message);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ApiError> handleBadInput(Exception ex) {
        return ErrorResponses.of(HttpStatus.BAD_REQUEST, "VALIDATION", "Requête invalide : paramètre ou corps mal formé");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ErrorResponses.of(HttpStatus.BAD_REQUEST, "VALIDATION", "Paramètre manquant : " + ex.getParameterName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return ErrorResponses.of(HttpStatus.METHOD_NOT_ALLOWED, "VALIDATION",
                "Méthode " + ex.getMethod() + " non autorisée ici");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiError> handleMediaType(HttpMediaTypeNotSupportedException ex) {
        return ErrorResponses.of(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "VALIDATION", "Le corps doit être envoyé en JSON");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> handleUnknownPath(NoResourceFoundException ex) {
        return ErrorResponses.of(HttpStatus.NOT_FOUND, "NOT_FOUND", "Adresse inconnue : /" + ex.getResourcePath());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex) {
        log.error("Erreur inattendue", ex);
        return ErrorResponses.of(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER", "Erreur interne du service");
    }
}
