package com.hackaton.auth.user.controller;

import static com.hackaton.auth.support.GatewayHeaders.withIdentity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.service.creator.UserCreator;
import com.hackaton.auth.user.service.reader.UserReader;
import com.hackaton.auth.user.service.updater.UserUpdater;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerAccountTest {

    private static final UserResponse DISABLED = new UserResponse(2L, "vente@erp.local", "Rasoa Andria", Role.VENTE, false,
            Instant.now());

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserCreator userCreator;
    @MockitoBean
    private UserReader userReader;
    @MockitoBean
    private UserUpdater userUpdater;

    @Test
    void shouldReturn200_whenRoleIsChanged() throws Exception {
        when(userUpdater.changeRole(2L, Role.STOCK, 1L)).thenReturn(DISABLED);

        mockMvc.perform(withIdentity(put("/users/2")).contentType(MediaType.APPLICATION_JSON).content("{\"role\":\"STOCK\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void shouldReturn400_whenRoleIsMissing() throws Exception {
        mockMvc.perform(withIdentity(put("/users/2")).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
        verifyNoInteractions(userUpdater);
    }

    @Test
    void shouldReturn200WithDisabledAccount_whenDisabling() throws Exception {
        when(userUpdater.setEnabled(2L, false, 1L)).thenReturn(DISABLED);

        mockMvc.perform(withIdentity(post("/users/2/disable")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @Test
    void shouldReturn422_whenAdminDisablesOwnAccount() throws Exception {
        when(userUpdater.setEnabled(1L, false, 1L)).thenThrow(new BusinessRuleException("Impossible"));

        mockMvc.perform(withIdentity(post("/users/1/disable")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn404_whenEnablingUnknownAccount() throws Exception {
        when(userUpdater.setEnabled(any(), any(Boolean.class), any())).thenThrow(new ResourceNotFoundException("Compte", 9L));

        mockMvc.perform(withIdentity(post("/users/9/enable")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn401_whenIdentityHeaderIsMissing() throws Exception {
        mockMvc.perform(post("/users/2/disable"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value("UNAUTHORIZED"));
    }
}
