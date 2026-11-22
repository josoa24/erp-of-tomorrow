package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;
import java.time.Instant;
import java.util.List;

public record UserResponse(Long id, String email, String fullName, List<Role> roles, Role role, boolean enabled,
                           Instant createdAt, Instant lastLoginAt) {
}
