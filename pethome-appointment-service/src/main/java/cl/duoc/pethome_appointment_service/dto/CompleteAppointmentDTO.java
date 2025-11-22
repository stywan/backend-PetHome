package cl.duoc.pethome_appointment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for completing an appointment with medical data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteAppointmentDTO {

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    @NotBlank(message = "Prescription is required")
    private String prescription;

    private String notes;
}
