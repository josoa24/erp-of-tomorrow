package com.hackaton.auth.login.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.auth.common.exception.InvalidCredentialsException;
import com.hackaton.auth.login.dto.LoginRequest;
import com.hackaton.auth.login.dto.LoginResponse;
import com.hackaton.auth.login.service.Authenticator;
import com.hackaton.auth.user.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(LoginController.class)
class LoginControllerTest {

    private static final String CREDENTIALS = "{\"email\":\"admin@erp.local\",\"password\":\"secret123\"}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private Authenticator authenticator;

    @Test
    void shouldReturn200WithToken_whenCredentialsAreValid() throws Exception {
        LoginRequest request = new LoginRequest("admin@erp.local", "secret123");
        when(authenticator.login(request)).thenReturn(new LoginResponse("jwt-token", "admin@erp.local", Role.ADMIN));

        login(CREDENTIALS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        when(authenticator.login(any())).thenThrow(new InvalidCredentialsException());

        login(CREDENTIALS)
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value("UNAUTHORIZED"));
    }

    @Test
    void shouldReturn400_whenPasswordIsMissing() throws Exception {
        login("{\"email\":\"admin@erp.local\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(authenticator);
    }

    private ResultActions login(String body) throws Exception {
        return mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
