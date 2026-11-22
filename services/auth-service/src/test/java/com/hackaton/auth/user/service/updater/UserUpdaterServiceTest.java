package com.hackaton.auth.user.service.updater;

import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserUpdaterServiceTest {

    private static final Long ADMIN_ID = 1L;
    private static final Long USER_ID = 2L;

    @Mock
    private UserAccountRepository userRepository;

    private UserUpdaterService service;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        service = new UserUpdaterService(userRepository, new UserMapper());
        user = new UserAccount();
        user.setId(USER_ID);
        user.setEmail("vente@erp.local");
        user.setRole(Role.VENTE);
    }

    @Test
    void shouldChangeRole_whenAccountExists() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserResponse response = service.changeRoles(USER_ID, Set.of(Role.STOCK), ADMIN_ID);

        assertEquals(Role.STOCK, response.role());
    }

    @Test
    void shouldDisableThenEnableAccount() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        assertFalse(service.setEnabled(USER_ID, false, ADMIN_ID).enabled());
        assertTrue(service.setEnabled(USER_ID, true, ADMIN_ID).enabled());
    }

    @Test
    void shouldRefuse_whenAdminTargetsOwnAccount() {
        assertThrows(BusinessRuleException.class, () -> service.setEnabled(ADMIN_ID, false, ADMIN_ID));
        assertThrows(BusinessRuleException.class, () -> service.changeRoles(ADMIN_ID, Set.of(Role.RH), ADMIN_ID));
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldThrowNotFound_whenAccountDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.changeRoles(99L, Set.of(Role.RH), ADMIN_ID));
    }
}
