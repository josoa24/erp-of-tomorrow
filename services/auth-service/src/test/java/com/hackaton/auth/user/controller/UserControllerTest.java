package com.hackaton.auth.user.controller;

import static com.hackaton.auth.support.GatewayHeaders.withIdentity;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.auth.common.exception.DuplicateResourceException;
import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.service.creator.UserCreator;
import com.hackaton.auth.user.service.reader.UserReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

    private static final String NEW_USER = "{\"email\":\"stock@erp.local\",\"password\":\"secret123\",\"role\":\"STOCK\"}";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserCreator userCreator;
    @MockitoBean
    private UserReader userReader;

    @Test
    void shouldReturn200WithUsers_whenListingAccounts() throws Exception {
        when(userReader.findAll()).thenReturn(List.of(user()));

        mockMvc.perform(withIdentity(get("/users")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].email").value("stock@erp.local"));
    }

    @Test
    void shouldReturn201WithCreatedUser_whenRequestIsValid() throws Exception {
        UserCreateRequest request = new UserCreateRequest("stock@erp.local", "secret123", Role.STOCK);
        when(userCreator.create(request)).thenReturn(user());

        mockMvc.perform(withIdentity(post("/users")).contentType(MediaType.APPLICATION_JSON).content(NEW_USER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.role").value("STOCK"));
    }

    @Test
    void shouldReturn409_whenEmailAlreadyExists() throws Exception {
        when(userCreator.create(any())).thenThrow(new DuplicateResourceException("Email deja utilise"));

        mockMvc.perform(withIdentity(post("/users")).contentType(MediaType.APPLICATION_JSON).content(NEW_USER))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    // Construit via Jackson pour ne pas dépendre de la liste des champs du record.
    private UserResponse user() throws Exception {
        return objectMapper.readValue("{\"id\":2,\"email\":\"stock@erp.local\",\"role\":\"STOCK\"}", UserResponse.class);
    }
}
