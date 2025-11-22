package cl.duoc.pethome_pet_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main application class for PetHome Pet Service.
 * This microservice handles all pet-related operations including
 * pet registration, medical records, and health tracking.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class PethomePetServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PethomePetServiceApplication.class, args);
	}

}
