package com.hackaton.stock.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String SCHEME = "bearerAuth";
    private static final List<String> ROLES = List.of("STOCK", "ADMIN");

    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock")
                        .version("1.0")
                        .description("Produits, mouvements, état de stock, fournisseurs et bons de commande. "
                                + "Rôles autorisés : " + String.join(", ", ROLES) + "."))
                .servers(List.of(new Server().url("/api/stock").description("Via la gateway")))
                .components(new Components().addSecuritySchemes(SCHEME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME));
    }

    @Bean
    public OperationCustomizer rolesCustomizer() {
        return (operation, handlerMethod) -> {
            String text = "Rôles autorisés : " + String.join(", ", ROLES);
            operation.setDescription(operation.getDescription() == null ? text : operation.getDescription() + "\n\n" + text);
            operation.addExtension("x-roles", ROLES);
            operation.setTags(List.of(humanize(handlerMethod.getBeanType().getSimpleName())));
            return operation;
        };
    }

    private static String humanize(String controllerName) {
        return controllerName.replace("Controller", "").replaceAll("([a-z])([A-Z])", "$1 $2");
    }
}
