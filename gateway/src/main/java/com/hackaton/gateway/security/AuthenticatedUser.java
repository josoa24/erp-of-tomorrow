package com.hackaton.gateway.security;

import java.util.List;

public record AuthenticatedUser(String email, Long userId, List<String> roles) {

    public String role() {
        return roles.get(0);
    }
}
