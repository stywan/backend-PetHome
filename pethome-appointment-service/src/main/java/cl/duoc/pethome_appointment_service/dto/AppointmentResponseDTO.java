package cl.duoc.pethome_appointment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for appointment responses with all details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponseDTO {

    private Long id;
    private Long clientId;
    private Long veterinarianId;
    private Long petId;
    private Long serviceId;

    private LocalDate date;
    private LocalTime time;
    private String duration;
    private String address;

    private String status;
    private String notes;

    // Medical data
    private String diagnosis;
    private String prescription;
    private LocalDateTime completedAt;

    // Cancellation
    private String cancellationReason;
    private LocalDateTime cancelledAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Related data (to be populated from other services)
    private Object client;
    private Object veterinarian;
    private Object pet;
    private Object service;
}
