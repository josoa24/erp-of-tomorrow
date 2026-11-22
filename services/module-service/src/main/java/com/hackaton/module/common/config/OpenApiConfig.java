package com.hackaton.module.common.config;

import com.hackaton.module.adaptation.controller.AdaptationController;
import com.hackaton.module.definition.controller.ModuleDefinitionController;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";
    private static final List<String> ALL_ROLES = List.of("STOCK", "VENTE", "RH", "CRM", "ADMIN");
    private static final List<String> ADMIN_ONLY = List.of("ADMIN");
    private static final Set<Class<?>> ADMIN_WRITES = Set.of(ModuleDefinitionController.class, AdaptationController.class);

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Modules")
                        .version("1.0")
                        .description("Modules créés par l'équipe (définitions, enregistrements, indicateurs) "
                                + "et adaptations activées. Lecture et enregistrements : tous les rôles ; "
                                + "création ou suppression d'un module et des adaptations : ADMIN."))
                .servers(List.of(new Server().url("/api/modules").description("Via la gateway")))
                .components(new Components().addSecuritySchemes(SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }

    @Bean
    public OperationCustomizer rolesCustomizer() {
        return (operation, handlerMethod) -> {
            List<String> roles = rolesOf(handlerMethod.getBeanType(), handlerMethod.getMethod());
            String text = "Rôles autorisés : " + String.join(", ", roles);
            operation.setDescription(operation.getDescription() == null ? text : operation.getDescription() + "\n\n" + text);
            operation.addExtension("x-roles", roles);
            operation.setTags(List.of(humanize(handlerMethod.getBeanType().getSimpleName())));
            return operation;
        };
    }

    static List<String> rolesOf(Class<?> controller, Method method) {
        boolean write = !method.isAnnotationPresent(GetMapping.class);
        return write && ADMIN_WRITES.contains(controller) ? ADMIN_ONLY : ALL_ROLES;
    }

    private static String humanize(String controllerName) {
        return controllerName.replace("Controller", "").replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
