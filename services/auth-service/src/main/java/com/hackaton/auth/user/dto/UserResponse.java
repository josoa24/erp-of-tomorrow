package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;
import java.time.Instant;

public record UserResponse(Long id, String email, String fullName, Role role, boolean enabled, Instant createdAt) {
}
