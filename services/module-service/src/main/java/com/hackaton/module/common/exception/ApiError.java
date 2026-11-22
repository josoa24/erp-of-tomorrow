package com.hackaton.module.common.exception;

import java.time.Instant;

public record ApiError(int status, String type, String message, Instant timestamp) {

    public static ApiError of(int status, String type, String message) {
        return new ApiError(status, type, message, Instant.now());
    }
}
