package com.hackaton.auth.user.service.updater;

import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;

public interface UserUpdater {

    UserResponse changeRole(Long id, Role role, Long actorId);

    UserResponse setEnabled(Long id, boolean enabled, Long actorId);
}
