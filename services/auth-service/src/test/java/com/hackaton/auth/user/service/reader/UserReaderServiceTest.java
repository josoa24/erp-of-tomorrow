package com.hackaton.auth.user.service.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserReaderServiceTest {

    @Mock
    private UserAccountRepository userRepository;

    private UserReaderService service;
    private UserAccount user;

    @BeforeEach
    void setUp() {
        service = new UserReaderService(userRepository, new UserMapper());
        user = new UserAccount();
        user.setId(3L);
        user.setEmail("toky@boutique-tana.mg");
        user.setFullName("Toky Rakoto");
        user.setRole(Role.VENTE);
    }

    @Test
    void shouldReturnAccountWithFullName_whenIdExists() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        UserResponse response = service.findById(3L);

        assertEquals("toky@boutique-tana.mg", response.email());
        assertEquals("Toky Rakoto", response.fullName());
        assertEquals(Role.VENTE, response.role());
    }

    @Test
    void shouldReturnNullFullName_whenAccountHasNoName() {
        user.setFullName(null);
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        assertNull(service.findById(3L).fullName());
    }

    @Test
    void shouldThrowNotFound_whenAccountDoesNotExist() {
        when(userRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void shouldMapEveryAccount_whenListingAll() {
        when(userRepository.findAll(org.mockito.ArgumentMatchers.any(org.springframework.data.domain.Sort.class))).thenReturn(List.of(user));

        List<UserResponse> responses = service.findAll();

        assertEquals(1, responses.size());
        assertEquals("Toky Rakoto", responses.get(0).fullName());
    }
}
