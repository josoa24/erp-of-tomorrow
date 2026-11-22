package com.hackaton.auth.user.dto;

import com.hackaton.auth.user.entity.Role;

public record UserSearchCriteria(String search, Role role, Boolean enabled, Integer page, Integer size) {

    public boolean isUnfiltered() {
        return (search == null || search.isBlank()) && role == null && enabled == null && page == null
                && size == null;
    }
}
