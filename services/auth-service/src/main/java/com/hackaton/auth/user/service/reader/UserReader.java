package com.hackaton.auth.user.service.reader;

import com.hackaton.auth.user.dto.UserResponse;
import java.util.List;

public interface UserReader {

    List<UserResponse> findAll();

    UserResponse findById(Long id);
}
