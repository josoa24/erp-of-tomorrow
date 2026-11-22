package com.hackaton.auth.user.service.password;

import com.hackaton.auth.user.dto.PasswordResetRequest;

public interface PasswordResetter {

    void reset(Long targetId, PasswordResetRequest request, Long actorId);
}
