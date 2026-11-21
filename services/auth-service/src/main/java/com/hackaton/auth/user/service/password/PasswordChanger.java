package com.hackaton.auth.user.service.password;

import com.hackaton.auth.user.dto.PasswordChangeRequest;

public interface PasswordChanger {

    void change(Long userId, PasswordChangeRequest request);
}
