package com.hackaton.auth.login.controller;

import com.hackaton.auth.login.dto.LoginRequest;
import com.hackaton.auth.login.dto.LoginResponse;
import com.hackaton.auth.login.service.Authenticator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final Authenticator authenticator;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authenticator.login(request);
    }
}
