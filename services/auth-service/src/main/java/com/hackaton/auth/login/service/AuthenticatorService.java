package com.hackaton.auth.login.service;

import com.hackaton.auth.common.exception.AccountDisabledException;
import com.hackaton.auth.common.exception.InvalidCredentialsException;
import com.hackaton.auth.login.dto.LoginRequest;
import com.hackaton.auth.login.dto.LoginResponse;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticatorService implements Authenticator {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenIssuer tokenIssuer;

    @Override
    public LoginResponse login(LoginRequest request) {
        UserAccount user = userRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        if (!user.isEnabled()) {
            throw new AccountDisabledException();
        }
        return new LoginResponse(tokenIssuer.issue(user), user.getEmail(), user.getFullName(), user.getRole());
    }
}
