package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;
import java.util.List;

public record UserSearchCriteria(String search, List<Role> roles, Boolean enabled, Integer page, Integer size) {

    public boolean isUnfiltered() {
        return (search == null || search.isBlank()) && (roles == null || roles.isEmpty()) && enabled == null && page == null
                && size == null;
    }
}
