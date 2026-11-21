package com.hackaton.auth.login.service;

import com.hackaton.auth.login.dto.LoginRequest;
import com.hackaton.auth.login.dto.LoginResponse;

public interface Authenticator {

    LoginResponse login(LoginRequest request);
}
