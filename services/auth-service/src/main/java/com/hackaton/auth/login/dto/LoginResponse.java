package com.hackaton.auth.login.dto;

import com.hackaton.auth.user.entity.Role;

public record LoginResponse(String token, String email, String fullName, Role role) {
}
