package com.hackaton.module.common.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hackaton.module.adaptation.controller.AdaptationController;
import com.hackaton.module.definition.controller.ModuleDefinitionController;
import com.hackaton.module.record.controller.ModuleRecordController;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    private static final List<String> ALL = List.of("STOCK", "VENTE", "RH", "CRM", "ADMIN");

    @Test
    void shouldDocumentAdminOnly_forDefinitionAndAdaptationWrites() {
        assertEquals(List.of("ADMIN"), roles(ModuleDefinitionController.class, "create"));
        assertEquals(List.of("ADMIN"), roles(AdaptationController.class, "deactivate"));
    }

    @Test
    void shouldDocumentEveryRole_forReadsAndRecords() {
        assertEquals(ALL, roles(ModuleDefinitionController.class, "findAll"));
        assertEquals(ALL, roles(ModuleRecordController.class, "create"));
    }

    private List<String> roles(Class<?> controller, String name) {
        Method method = Arrays.stream(controller.getMethods()).filter(candidate -> candidate.getName().equals(name))
                .findFirst().orElseThrow();
        return OpenApiConfig.rolesOf(controller, method);
    }
}
