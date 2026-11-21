package com.hackaton.auth.user.controller;

import com.hackaton.auth.common.identity.IdentityHeaders;
import com.hackaton.auth.user.dto.PasswordChangeRequest;
import com.hackaton.auth.user.service.password.PasswordChanger;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordChanger passwordChanger;

    @PostMapping("/users/me/password")
    public ResponseEntity<Void> change(@Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long userId,
                                       @Valid @RequestBody PasswordChangeRequest request) {
        passwordChanger.change(userId, request);
        return ResponseEntity.noContent().build();
    }
}
