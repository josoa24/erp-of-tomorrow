package com.hackaton.module.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

final class ErrorResponses {

    private ErrorResponses() {
    }

    static ResponseEntity<ApiError> of(HttpStatus status, String type, String message) {
        return ResponseEntity.status(status).body(ApiError.of(status.value(), type, message));
    }
}
