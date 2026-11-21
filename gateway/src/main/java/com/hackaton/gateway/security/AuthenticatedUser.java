package com.hackaton.gateway.security;

public record AuthenticatedUser(String email, Long userId, String role) {
}
