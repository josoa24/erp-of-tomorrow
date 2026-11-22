package com.hackaton.auth.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.RolePriority;
import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import java.util.Set;

public record UserRolesUpdateRequest(List<Role> roles, Role role) {

    @JsonIgnore
    @AssertTrue(message = "au moins un rôle est requis")
    public boolean isRolesProvided() {
        return !effectiveRoles().isEmpty();
    }

    @JsonIgnore
    public Set<Role> effectiveRoles() {
        return RolePriority.merge(roles, role);
    }
}
