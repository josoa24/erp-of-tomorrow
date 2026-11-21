package com.hackaton.auth.user.controller;

import static com.hackaton.auth.support.GatewayHeaders.withIdentity;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.auth.user.service.creator.UserCreator;
import com.hackaton.auth.user.service.reader.UserReader;
import com.hackaton.auth.user.service.updater.UserUpdater;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(UserController.class)
class UserControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserCreator userCreator;
    @MockitoBean
    private UserReader userReader;
    @MockitoBean
    private UserUpdater userUpdater;

    @Test
    void shouldReturn400_whenPasswordIsTooShort() throws Exception {
        createUser("{\"email\":\"stock@erp.local\",\"password\":\"short\",\"role\":\"STOCK\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(userCreator);
    }

    @Test
    void shouldReturn400_whenRoleIsMissing() throws Exception {
        createUser("{\"email\":\"stock@erp.local\",\"password\":\"secret123\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(userCreator);
    }

    @Test
    void shouldReturn400_whenEmailIsInvalid() throws Exception {
        createUser("{\"email\":\"not-an-email\",\"password\":\"secret123\",\"role\":\"STOCK\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(userCreator);
    }

    @Test
    void shouldReturn400Validation_whenRoleIsUnknown() throws Exception {
        createUser("{\"email\":\"stock@erp.local\",\"password\":\"secret123\",\"role\":\"PATRON\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(userCreator);
    }

    @Test
    void shouldReturn400Validation_whenJsonIsMalformed() throws Exception {
        createUser("{\"email\":")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));
    }

    private ResultActions createUser(String body) throws Exception {
        return mockMvc.perform(withIdentity(post("/users")).contentType(MediaType.APPLICATION_JSON).content(body));
    }
}
