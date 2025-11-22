package cl.duoc.pethome_appointment_service.config;

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

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI appointmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetHome Appointment Service API")
                        .description("Microservicio de gestión de citas veterinarias para la plataforma PetHome. " +
                                "Proporciona endpoints para agendar, modificar y consultar citas veterinarias.")
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
                                .description("Appointment Service - Desarrollo"),
                        new Server()
                                .url("http://localhost:8080/api/appointments")
                                .description("API Gateway - Producción")
                ));
    }
}
