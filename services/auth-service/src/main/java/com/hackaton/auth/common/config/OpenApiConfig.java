package com.hackaton.auth.common.config;

import com.hackaton.auth.login.controller.LoginController;
import com.hackaton.auth.user.controller.PasswordController;
import com.hackaton.auth.user.entity.Role;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.Arrays;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";
    private static final List<String> ADMIN_ONLY = List.of("ADMIN");
    private static final List<String> ALL_ROLES = Arrays.stream(Role.values()).map(Role::name).toList();

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth")
                        .version("1.0")
                        .description("Connexion et comptes utilisateurs. La connexion est publique ; "
                                + "la gestion des comptes est réservée au rôle ADMIN."))
                .servers(List.of(new Server().url("/api/auth").description("Via la gateway")))
                .components(new Components().addSecuritySchemes(SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }

    @Bean
    public OperationCustomizer rolesCustomizer() {
        return (operation, handlerMethod) -> {
            boolean publicOperation = handlerMethod.getBeanType().equals(LoginController.class);
            boolean selfService = handlerMethod.getBeanType().equals(PasswordController.class);
            List<String> roles = publicOperation ? List.of() : selfService ? ALL_ROLES : ADMIN_ONLY;
            String text = publicOperation
                    ? "Accès public : aucun token requis. Renvoie le token JWT à utiliser dans Authorize."
                    : "Rôles autorisés : " + String.join(", ", roles);
            operation.setDescription(operation.getDescription() == null ? text : operation.getDescription() + "\n\n" + text);
            operation.addExtension("x-roles", roles);
            if (publicOperation) {
                operation.setSecurity(List.of());
            }
            operation.setTags(List.of(humanize(handlerMethod.getBeanType().getSimpleName())));
            return operation;
        };
    }

    private static String humanize(String controllerName) {
        return controllerName.replace("Controller", "").replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
