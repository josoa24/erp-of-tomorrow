package com.hackaton.module.definition.controller;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.module.common.exception.ConflictException;
import com.hackaton.module.common.exception.ForbiddenException;
import com.hackaton.module.common.exception.ResourceNotFoundException;
import com.hackaton.module.definition.dto.ModuleSummary;
import com.hackaton.module.definition.mapper.DefinitionPartsMapper;
import com.hackaton.module.definition.mapper.ModuleDefinitionMapper;
import com.hackaton.module.support.DefinitionFixtures;
import com.hackaton.module.support.GatewayHeaders;
import com.hackaton.module.support.RequestFixtures;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ModuleDefinitionControllerTest extends DefinitionControllerTestBase {

    private final ModuleDefinitionMapper mapper = new ModuleDefinitionMapper(new DefinitionPartsMapper());

    @Test
    void shouldReturn200WithSummaries_whenListingDefinitions() throws Exception {
        when(definitionReader.findAll()).thenReturn(List.of(new ModuleSummary(10L, "livraisons", "Livraisons", "truck", null)));

        mockMvc.perform(asSeller(get("/definitions")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("livraisons"));
    }

    @Test
    void shouldReturn200WithFinalFlag_whenDefinitionExists() throws Exception {
        when(definitionReader.findBySlug("livraisons")).thenReturn(mapper.toResponse(DefinitionFixtures.livraisons()));

        mockMvc.perform(asSeller(get("/definitions/livraisons")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statuses[2].final").value(true))
                .andExpect(jsonPath("$.columns[0].reference").value("CUSTOMER"));
    }

    @Test
    void shouldReturn404_whenDefinitionIsUnknown() throws Exception {
        when(definitionReader.findBySlug("inconnu")).thenThrow(new ResourceNotFoundException("Module introuvable"));

        mockMvc.perform(asSeller(get("/definitions/inconnu")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("NOT_FOUND"));
    }

    @Test
    void shouldReturn201AndPassCaller_whenAdminCreatesDefinition() throws Exception {
        when(definitionCreator.create(any(), argThat(caller -> caller.isAdmin()
                && GatewayHeaders.ADMIN_EMAIL.equals(caller.email()))))
                .thenReturn(mapper.toResponse(DefinitionFixtures.livraisons()));

        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(body()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.slug").value("livraisons"));
    }

    @Test
    void shouldReturn403Forbidden_whenSellerCreatesDefinition() throws Exception {
        when(definitionCreator.create(any(), any())).thenThrow(new ForbiddenException("Réservé au gérant"));

        mockMvc.perform(asSeller(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(body()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn409_whenSlugAlreadyExists() throws Exception {
        when(definitionCreator.create(any(), any())).thenThrow(new ConflictException("Un module porte déjà ce nom"));

        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON).content(body()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    private String body() throws Exception {
        return new ObjectMapper().writeValueAsString(RequestFixtures.livraisons());
    }
}
