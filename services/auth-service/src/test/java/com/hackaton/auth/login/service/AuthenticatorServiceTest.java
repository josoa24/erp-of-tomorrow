package com.hackaton.auth.login.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.auth.common.exception.AccountDisabledException;
import com.hackaton.auth.common.exception.InvalidCredentialsException;
import com.hackaton.auth.login.dto.LoginRequest;
import com.hackaton.auth.login.dto.LoginResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticatorServiceTest {

    private static final String SECRET = "test-secret-0123456789abcdef-0123456789abcdef";

    @Mock
    private UserAccountRepository userRepository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private AuthenticatorService service;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        service = new AuthenticatorService(userRepository, encoder, new JwtTokenIssuer(SECRET, 60),
                userId -> { });
        user = new UserAccount();
        user.setId(1L);
        user.setEmail("rh@erp.local");
        user.setPasswordHash(encoder.encode("motdepasse1"));
        user.setRole(Role.RH);
    }

    @Test
    void shouldReturnToken_whenCredentialsAreValid() {
        when(userRepository.findByEmailIgnoreCase("rh@erp.local")).thenReturn(Optional.of(user));

        LoginResponse response = service.login(new LoginRequest("rh@erp.local", "motdepasse1"));

        assertFalse(response.token().isBlank());
        assertEquals(Role.RH, response.role());
    }

    @Test
    void shouldReturnFullName_whenAccountHasOne() {
        user.setFullName("Rasoa Andria");
        when(userRepository.findByEmailIgnoreCase("rh@erp.local")).thenReturn(Optional.of(user));

        LoginResponse response = service.login(new LoginRequest("rh@erp.local", "motdepasse1"));

        assertEquals("Rasoa Andria", response.fullName());
    }

    @Test
    void shouldThrowInvalidCredentials_whenPasswordIsWrong() {
        when(userRepository.findByEmailIgnoreCase("rh@erp.local")).thenReturn(Optional.of(user));

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginRequest("rh@erp.local", "mauvais")));
    }

    @Test
    void shouldRefuseLogin_whenAccountIsDisabled() {
        user.setEnabled(false);
        when(userRepository.findByEmailIgnoreCase("rh@erp.local")).thenReturn(Optional.of(user));

        assertThrows(AccountDisabledException.class,
                () -> service.login(new LoginRequest("rh@erp.local", "motdepasse1")));
    }

    @Test
    void shouldThrowInvalidCredentials_whenEmailIsUnknown() {
        when(userRepository.findByEmailIgnoreCase("inconnu@erp.local")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.login(new LoginRequest("inconnu@erp.local", "motdepasse1")));
    }
}
