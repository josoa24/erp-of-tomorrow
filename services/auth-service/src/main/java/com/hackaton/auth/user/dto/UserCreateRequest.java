package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotNull Role role,
        @Size(max = 120) String fullName) {
}
