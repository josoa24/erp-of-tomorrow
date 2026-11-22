package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(@NotNull Role role) {
}
