package com.hackaton.auth.user.service.creator;

import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.dto.UserResponse;

public interface UserCreator {

    UserResponse create(UserCreateRequest request);
}
