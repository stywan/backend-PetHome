package cl.duoc.pethome_pet_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO for creating a new medical record.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequestDTO {

    @NotNull(message = "Pet ID is required")
    private Long petId;

    @NotNull(message = "Veterinarian ID is required")
    private Long veterinarianId;

    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;

    @NotBlank(message = "Record type is required")
    @Pattern(regexp = "^(CHECKUP|VACCINATION|SURGERY|TREATMENT|EMERGENCY|OTHER)$",
             message = "Type must be CHECKUP, VACCINATION, SURGERY, TREATMENT, EMERGENCY, or OTHER")
    private String recordType;

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 5, max = 500, message = "Diagnosis must be between 5 and 500 characters")
    private String diagnosis;

    @Size(max = 1000, message = "Treatment must not exceed 1000 characters")
    private String treatment;

    @Size(max = 500, message = "Medications must not exceed 500 characters")
    private String medications;

    private String notes;

    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be positive or zero")
    private Double weight;

    @DecimalMin(value = "0.0", inclusive = true, message = "Temperature must be positive or zero")
    private Double temperature;

    @Min(value = 0, message = "Heart rate must be positive or zero")
    private Integer heartRate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be positive or zero")
    private Double cost;

    private LocalDate nextVisitDate;
}
