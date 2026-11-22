package com.hackaton.auth.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.repository.UserAccountRepository;
import com.hackaton.auth.user.service.creator.UserCreator;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AdminSeederTest {

    private static final String EMAIL = "admin@erp.local";

    @Mock
    private UserAccountRepository userRepository;
    @Mock
    private UserCreator userCreator;

    @Test
    void shouldCreateNamedAdmin_whenNoAccountExists() {
        when(userRepository.count()).thenReturn(0L);

        seeder("Hery Rabe").run();

        verify(userCreator).create(new UserCreateRequest(EMAIL, "admin1234", Role.ADMIN, "Hery Rabe"));
    }

    @Test
    void shouldCompleteAdminName_whenExistingAdminHasNone() {
        UserAccount admin = account(null);
        when(userRepository.count()).thenReturn(1L);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin));

        seeder(" Hery Rabe ").run();

        assertEquals("Hery Rabe", admin.getFullName());
        verifyNoInteractions(userCreator);
    }

    @Test
    void shouldKeepExistingName_whenAdminAlreadyHasOne() {
        UserAccount admin = account("Rasoa Andria");
        when(userRepository.count()).thenReturn(1L);
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(admin));

        seeder("Hery Rabe").run();

        assertEquals("Rasoa Andria", admin.getFullName());
    }

    @Test
    void shouldLeaveAccountsUntouched_whenConfiguredNameIsBlank() {
        when(userRepository.count()).thenReturn(1L);

        seeder(" ").run();

        verify(userRepository, never()).findByEmail(anyString());
    }

    private AdminSeeder seeder(String name) {
        return new AdminSeeder(userRepository, userCreator, EMAIL, "admin1234", name);
    }

    private UserAccount account(String fullName) {
        UserAccount account = new UserAccount();
        account.setEmail(EMAIL);
        account.setRole(Role.ADMIN);
        account.setFullName(fullName);
        return account;
    }
}
