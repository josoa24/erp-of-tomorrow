package com.hackaton.module.definition.controller;

import static com.hackaton.module.support.GatewayHeaders.asAdmin;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hackaton.module.common.exception.BusinessException;
import com.hackaton.module.common.exception.ForbiddenException;
import org.junit.jupiter.api.Test;

class ModuleDefinitionDeleteTest extends DefinitionControllerTestBase {

    @Test
    void shouldReturn204_whenAdminDeletesDefinition() throws Exception {
        mockMvc.perform(asAdmin(delete("/definitions/livraisons"))).andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn422_whenDefinitionStillHasRecords() throws Exception {
        doThrow(new BusinessException("Le module contient des enregistrements"))
                .when(definitionDeleter).delete(any(), any());

        mockMvc.perform(asAdmin(delete("/definitions/livraisons")))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("BUSINESS"));
    }

    @Test
    void shouldReturn403_whenDeleterRefusesTheRole() throws Exception {
        doThrow(new ForbiddenException("Réservé au gérant")).when(definitionDeleter).delete(any(), any());

        mockMvc.perform(asAdmin(delete("/definitions/livraisons")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.type").value("FORBIDDEN"));
    }
}
