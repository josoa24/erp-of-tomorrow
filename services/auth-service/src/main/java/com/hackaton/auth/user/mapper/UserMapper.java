package com.hackaton.auth.user.mapper;

import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.UserAccount;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(UserAccount user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(),
                user.sortedRoles(), user.getRole(), user.isEnabled(),
                user.getCreatedAt(), user.getLastLoginAt());
    }
}
