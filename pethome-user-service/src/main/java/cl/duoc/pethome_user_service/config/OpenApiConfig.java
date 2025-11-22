package cl.duoc.pethome_user_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for User Service.
 * Provides API documentation with JWT authentication support.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI userServiceOpenAPI() {
        // Define JWT security scheme
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("PetHome User Service API")
                        .description("Microservicio de gestión de usuarios y autenticación JWT para la plataforma PetHome. " +
                                "Proporciona endpoints para registro, login, gestión de usuarios y perfiles.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("PetHome Development Team")
                                .email("pethome@duoc.cl"))
                        .license(new License()
                                .name("Proyecto Académico")
                                .url("https://duoc.cl")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("User Service - Desarrollo"),
                        new Server()
                                .url("http://localhost:8080/api/users")
                                .description("API Gateway - Producción")
                ))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Ingresa el token JWT obtenido del endpoint /auth/login")));
    }
}
