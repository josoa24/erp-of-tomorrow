package com.hackaton.auth.user.controller;

import com.hackaton.auth.common.identity.IdentityHeaders;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.service.reader.UserReader;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CurrentUserController {

    private final UserReader userReader;

    @GetMapping("/users/me")
    public UserResponse me(@Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long userId) {
        return userReader.findById(userId);
    }
}
