package com.hackaton.auth.user.controller;

import static com.hackaton.auth.support.GatewayHeaders.withIdentity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.user.dto.PasswordChangeRequest;
import com.hackaton.auth.user.service.password.PasswordChanger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PasswordController.class)
class PasswordControllerTest {

    private static final String PATH = "/users/me/password";
    private static final String BODY = "{\"currentPassword\":\"ancien-mdp1\",\"newPassword\":\"nouveau-mdp1\"}";

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PasswordChanger passwordChanger;

    @Test
    void shouldReturn204AndUseCallerId_whenPasswordIsChanged() throws Exception {
        mockMvc.perform(withIdentity(post(PATH)).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNoContent());

        verify(passwordChanger).change(1L, new PasswordChangeRequest("ancien-mdp1", "nouveau-mdp1"));
    }

    @Test
    void shouldReturn400_whenNewPasswordIsTooShort() throws Exception {
        String body = "{\"currentPassword\":\"ancien-mdp1\",\"newPassword\":\"court\"}";

        mockMvc.perform(withIdentity(post(PATH)).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
        verifyNoInteractions(passwordChanger);
    }

    @Test
    void shouldReturn422_whenCurrentPasswordIsWrong() throws Exception {
        doThrow(new BusinessRuleException("Le mot de passe actuel est incorrect"))
                .when(passwordChanger).change(eq(1L), any());

        mockMvc.perform(withIdentity(post(PATH)).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }
}
