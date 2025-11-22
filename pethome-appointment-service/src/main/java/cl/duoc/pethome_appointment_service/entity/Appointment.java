package cl.duoc.pethome_appointment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing an appointment in the PetHome system.
 * Manages veterinary appointments with clients, pets, and services.
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Client ID is required")
    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @NotNull(message = "Veterinarian ID is required")
    @Column(name = "veterinarian_id", nullable = false)
    private Long veterinarianId;

    @NotNull(message = "Pet ID is required")
    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @NotNull(message = "Service ID is required")
    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    // Scheduling
    @NotNull(message = "Date is required")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "Time is required")
    @Column(nullable = false)
    private LocalTime time;

    @Column(length = 20)
    private String duration; // e.g., "45 min"

    // Location
    @Column(length = 500)
    private String address;

    // Status
    @NotBlank(message = "Status is required")
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "pending"; // pending, confirmed, completed, cancelled

    // Client Notes
    @Column(length = 1000)
    private String notes;

    // Medical Data (populated when completed)
    @Column(length = 2000)
    private String diagnosis;

    @Column(length = 2000)
    private String prescription;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    // Cancellation
    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Timestamps
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
