package cl.duoc.pethome_veterinarian_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for PetHome Veterinarian Service.
 * This microservice handles all veterinarian-related operations including
 * registration, profile management, and scheduling.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class PethomeVeterinarianServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PethomeVeterinarianServiceApplication.class, args);
	}

}
