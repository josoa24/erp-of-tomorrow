package com.hackaton.auth.user.controller;

import java.util.List;
import static com.hackaton.auth.support.GatewayHeaders.withIdentity;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.service.reader.UserReader;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CurrentUserController.class)
class CurrentUserControllerTest {

    private static final String PATH = "/users/me";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserReader userReader;

    @Test
    void shouldReturnCallerAccount_whenIdentityIsPresent() throws Exception {
        when(userReader.findById(1L)).thenReturn(
                new UserResponse(1L, "hery@boutique-tana.mg", "Hery Rabe", List.of(Role.ADMIN), Role.ADMIN, true, Instant.now(), null));

        mockMvc.perform(withIdentity(get(PATH)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("hery@boutique-tana.mg"))
                .andExpect(jsonPath("$.fullName").value("Hery Rabe"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturn404_whenCallerAccountNoLongerExists() throws Exception {
        when(userReader.findById(1L)).thenThrow(new ResourceNotFoundException("Compte", 1L));

        mockMvc.perform(withIdentity(get(PATH)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn401_whenIdentityHeaderIsMissing() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value("UNAUTHORIZED"));

        verifyNoInteractions(userReader);
    }
}
