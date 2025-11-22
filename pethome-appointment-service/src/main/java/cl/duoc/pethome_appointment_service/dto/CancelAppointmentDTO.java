package cl.duoc.pethome_appointment_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cancelling an appointment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelAppointmentDTO {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;
}
