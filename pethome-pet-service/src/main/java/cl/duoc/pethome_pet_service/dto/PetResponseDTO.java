package cl.duoc.pethome_pet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for pet response data.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetResponseDTO {

    private Long id;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private String gender;
    private String color;
    private Double weight;
    private String medicalConditions;
    private String allergies;
    private Long ownerId;
    private Boolean isActive;
    private Integer age;
    private String formattedAge;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
