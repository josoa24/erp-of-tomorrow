package com.hackaton.module.definition.controller;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hackaton.module.support.RequestFixtures;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ModuleDefinitionValidationTest extends DefinitionControllerTestBase {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldReturn400_whenNameIsTooShort() throws Exception {
        expectValidationError(body -> body.put("name", "L"));
    }

    @Test
    void shouldReturn400_whenIconIsNotInAllowList() throws Exception {
        expectValidationError(body -> body.put("icon", "rocket"));
    }

    @Test
    void shouldReturn400_whenOnlyOneStatusIsGiven() throws Exception {
        expectValidationError(body -> {
            ArrayNode statuses = (ArrayNode) body.get("statuses");
            statuses.remove(2);
            statuses.remove(1);
        });
    }

    @Test
    void shouldReturn400_whenColumnsAreMissing() throws Exception {
        expectValidationError(body -> body.remove("columns"));
    }

    @Test
    void shouldReturn400_whenColumnKeyIsNotSnakeCase() throws Exception {
        expectValidationError(body -> ((ObjectNode) body.get("columns").get(0)).put("key", "Nom Client"));
    }

    @Test
    void shouldReturn400_whenColumnTypeIsUnknown() throws Exception {
        expectValidationError(body -> ((ObjectNode) body.get("columns").get(0)).put("type", "IMAGE"));
    }

    @Test
    void shouldReturn401_whenGatewayHeadersAreMissing() throws Exception {
        mockMvc.perform(get("/definitions"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").value("UNAUTHORIZED"));
    }

    private void expectValidationError(Consumer<ObjectNode> change) throws Exception {
        ObjectNode body = objectMapper.valueToTree(RequestFixtures.livraisons());
        change.accept(body);

        mockMvc.perform(asAdmin(post("/definitions")).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("VALIDATION"));

        verifyNoInteractions(definitionCreator);
    }
}
