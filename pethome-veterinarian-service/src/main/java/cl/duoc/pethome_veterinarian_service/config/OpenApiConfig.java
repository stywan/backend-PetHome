package cl.duoc.pethome_veterinarian_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Veterinarian Service.
 * Provides API documentation for veterinarian management endpoints.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI veterinarianServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetHome Veterinarian Service API")
                        .description("Microservicio de gestión de veterinarios para la plataforma PetHome. " +
                                "Proporciona endpoints para registro de veterinarios, gestión de perfiles, " +
                                "especialidades, horarios y búsqueda.")
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
                                .description("Veterinarian Service - Desarrollo"),
                        new Server()
                                .url("http://localhost:8080/api/veterinarians")
                                .description("API Gateway - Producción")
                ));
    }
}
