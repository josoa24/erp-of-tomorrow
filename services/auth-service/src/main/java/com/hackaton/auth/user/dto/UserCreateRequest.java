package com.hackaton.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.RolePriority;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Set;

public record UserCreateRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        List<Role> roles,
        Role role,
        @Size(max = 120) String fullName) {

    public UserCreateRequest {
        email = email == null ? null : email.strip();
    }

    @JsonIgnore
    @AssertTrue(message = "au moins un rôle est requis")
    public boolean isRolesProvided() {
        return !effectiveRoles().isEmpty();
    }

    @JsonIgnore
    public Set<Role> effectiveRoles() {
        return RolePriority.merge(roles, role);
    }

    @Override
    public String toString() {
        return "UserCreateRequest[email=" + email + ", password=***, roles=" + effectiveRoles() + "]";
    }
}
