package com.hackaton.auth.user.controller;

import com.hackaton.auth.common.identity.IdentityHeaders;
import com.hackaton.auth.user.dto.PasswordResetRequest;
import com.hackaton.auth.user.service.password.PasswordResetter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserPasswordResetController {

    private final PasswordResetter passwordResetter;

    @Operation(summary = "Réinitialise le mot de passe d'un autre compte (administrateur)")
    @PostMapping("/users/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(@PathVariable Long id, @Valid @RequestBody PasswordResetRequest request,
                      @Parameter(hidden = true) @RequestHeader(IdentityHeaders.USER_ID) Long actorId) {
        passwordResetter.reset(id, request, actorId);
    }
}
