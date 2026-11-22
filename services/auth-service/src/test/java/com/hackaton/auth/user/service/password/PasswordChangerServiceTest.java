package com.hackaton.auth.user.service.password;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.PasswordChangeRequest;
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
class PasswordChangerServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private PasswordChangerService service;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        service = new PasswordChangerService(userRepository, encoder);
        user = new UserAccount();
        user.setId(1L);
        user.setPasswordHash(encoder.encode("ancien-mdp1"));
    }

    @Test
    void shouldStoreNewHash_whenCurrentPasswordIsCorrect() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        service.change(1L, new PasswordChangeRequest("ancien-mdp1", "nouveau-mdp1"));

        assertTrue(encoder.matches("nouveau-mdp1", user.getPasswordHash()));
    }

    @Test
    void shouldRefuse_whenCurrentPasswordIsWrong() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class,
                () -> service.change(1L, new PasswordChangeRequest("mauvais", "nouveau-mdp1")));
        assertTrue(encoder.matches("ancien-mdp1", user.getPasswordHash()));
    }

    @Test
    void shouldRefuse_whenNewPasswordEqualsCurrent() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BusinessRuleException.class,
                () -> service.change(1L, new PasswordChangeRequest("ancien-mdp1", "ancien-mdp1")));
    }

    @Test
    void shouldThrowNotFound_whenAccountDoesNotExist() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.change(9L, new PasswordChangeRequest("ancien-mdp1", "nouveau-mdp1")));
    }
}
