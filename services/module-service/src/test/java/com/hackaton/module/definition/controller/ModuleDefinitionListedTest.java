package com.hackaton.module.definition.controller;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static com.hackaton.module.support.GatewayHeaders.asSeller;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hackaton.module.definition.dto.ColumnSpec;
import com.hackaton.module.definition.dto.ModuleDefinitionRequest;
import com.hackaton.module.definition.dto.ModuleDefinitionResponse;
import com.hackaton.module.definition.entity.ColumnType;
import com.hackaton.module.support.RequestFixtures;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;

class ModuleDefinitionListedTest extends DefinitionControllerTestBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReadListedFlag_andDefaultMissingFlagToListed() throws Exception {
        ObjectNode body = objectMapper.valueToTree(RequestFixtures.livraisons());
        ((ObjectNode) body.get("columns").get(4)).put("listed", false);
        ((ObjectNode) body.get("columns").get(1)).remove("listed");

        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)));

        ArgumentCaptor<ModuleDefinitionRequest> request = ArgumentCaptor.forClass(ModuleDefinitionRequest.class);
        verify(definitionCreator).create(request.capture(), any());
        assertEquals(false, request.getValue().columns().get(4).shownInList());
        assertEquals(true, request.getValue().columns().get(1).shownInList());
    }

    @Test
    void shouldExposeListedFlag_whenReadingADefinition() throws Exception {
        when(definitionReader.findBySlug("livraisons")).thenReturn(new ModuleDefinitionResponse(1L, "livraisons",
                "Livraisons", "truck", null, List.of(
                        new ColumnSpec("quartier", "Quartier", ColumnType.TEXT, false, null, null, true),
                        new ColumnSpec("signature", "Signature", ColumnType.TEXT, false, null, null, false)),
                RequestFixtures.livraisons().statuses(), List.of(), "hery.rabe@nova.mg", Instant.EPOCH));

        mockMvc.perform(asSeller(get("/definitions/livraisons")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.columns[0].listed").value(true))
                .andExpect(jsonPath("$.columns[1].listed").value(false));
    }
}
