package cl.duoc.pethome_pet_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private String serverPort;

    @Bean
    public OpenAPI petServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetHome Pet Service API")
                        .description("Microservicio de gestión de mascotas para la plataforma PetHome. " +
                                "Proporciona endpoints para registro de mascotas, fichas médicas e historial de salud.")
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
                                .description("Pet Service - Desarrollo"),
                        new Server()
                                .url("http://localhost:8080/api/pets")
                                .description("API Gateway - Producción")
                ));
    }
}
