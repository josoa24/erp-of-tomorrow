package com.hackaton.auth.user.service.creator;

import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hackaton.auth.common.exception.DuplicateResourceException;
import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserCreatorServiceTest {

    private static final String EMAIL = "rasoa@boutique-tana.mg";

    @Mock
    private UserAccountRepository userRepository;

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    private UserCreatorService service;

    @BeforeEach
    void setUp() {
        service = new UserCreatorService(userRepository, encoder, new UserMapper());
    }

    @Test
    void shouldStoreTrimmedFullNameAndHashedPassword_whenRequestIsValid() {
        when(userRepository.save(any(UserAccount.class))).thenAnswer(call -> call.getArgument(0));

        UserResponse response = service.create(new UserCreateRequest(EMAIL, "motdepasse1", List.of(Role.RH), null, "  Rasoa Andria "));

        assertEquals("Rasoa Andria", response.fullName());
        assertEquals(Role.RH, response.role());
        verify(userRepository).save(argThat(user -> encoder.matches("motdepasse1", user.getPasswordHash())));
    }

    @Test
    void shouldStoreNullFullName_whenNameIsBlank() {
        when(userRepository.save(any(UserAccount.class))).thenAnswer(call -> call.getArgument(0));

        UserResponse response = service.create(new UserCreateRequest(EMAIL, "motdepasse1", List.of(Role.RH), null, "   "));

        assertNull(response.fullName());
    }

    @Test
    void shouldRefuse_whenEmailAlreadyExists() {
        when(userRepository.existsByEmailIgnoreCase(EMAIL)).thenReturn(true);

        DuplicateResourceException error = assertThrows(DuplicateResourceException.class,
                () -> service.create(new UserCreateRequest(EMAIL, "motdepasse1", List.of(Role.RH), null, "Rasoa Andria")));

        assertTrue(error.getMessage().contains(EMAIL));
        verify(userRepository, never()).save(any());
    }
}
