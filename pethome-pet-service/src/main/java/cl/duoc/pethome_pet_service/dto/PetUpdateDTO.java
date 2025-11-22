package cl.duoc.pethome_pet_service.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating pet information.
 * All fields are optional.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetUpdateDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 50, message = "Breed must not exceed 50 characters")
    private String breed;

    @Pattern(regexp = "^(MALE|FEMALE|UNKNOWN)$", message = "Gender must be MALE, FEMALE, or UNKNOWN")
    private String gender;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    private String color;

    @Positive(message = "Weight must be positive")
    private Double weight;

    private String medicalConditions;

    private String allergies;

    private Boolean isActive;
}
