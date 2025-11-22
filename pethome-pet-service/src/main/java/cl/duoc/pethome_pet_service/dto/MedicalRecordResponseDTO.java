package cl.duoc.pethome_pet_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for medical record response data.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordResponseDTO {

    private Long id;
    private Long petId;
    private String petName;
    private Long veterinarianId;
    private LocalDate visitDate;
    private String recordType;
    private String diagnosis;
    private String treatment;
    private String medications;
    private String notes;
    private Double weight;
    private Double temperature;
    private Integer heartRate;
    private Double cost;
    private LocalDate nextVisitDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
