package com.hackaton.auth.user.service.updater;

import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import java.util.Set;

public interface UserUpdater {

    UserResponse changeRoles(Long id, Set<Role> roles, Long actorId);

    UserResponse setEnabled(Long id, boolean enabled, Long actorId);
}
