package cl.duoc.pethome_pet_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for creating a new pet.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetRequestDTO {

    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;

    @Size(max = 50, message = "Breed must not exceed 50 characters")
    private String breed;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|UNKNOWN)$", message = "Gender must be MALE, FEMALE, or UNKNOWN")
    private String gender;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Positive(message = "Weight must be positive")
    private Double weight;

    private String medicalConditions;

    private String allergies;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
