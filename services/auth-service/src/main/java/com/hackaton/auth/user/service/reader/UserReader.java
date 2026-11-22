package com.hackaton.auth.user.service.reader;

import com.hackaton.auth.user.dto.UserPage;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.dto.UserSearchCriteria;
import java.util.List;

public interface UserReader {

    List<UserResponse> findAll();

    UserPage search(UserSearchCriteria criteria);

    UserResponse findById(Long id);
}
