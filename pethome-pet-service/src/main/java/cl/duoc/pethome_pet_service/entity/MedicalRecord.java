package cl.duoc.pethome_pet_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a medical record for a pet.
 * Contains information about veterinary visits, treatments, and vaccinations.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Pet is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @NotNull(message = "Veterinarian ID is required")
    @Column(nullable = false)
    private Long veterinarianId;

    @NotNull(message = "Visit date is required")
    @Column(nullable = false)
    private LocalDate visitDate;

    @NotBlank(message = "Record type is required")
    @Pattern(regexp = "^(CHECKUP|VACCINATION|SURGERY|TREATMENT|EMERGENCY|OTHER)$",
             message = "Type must be CHECKUP, VACCINATION, SURGERY, TREATMENT, EMERGENCY, or OTHER")
    @Column(nullable = false, length = 20)
    private String recordType;

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 5, max = 500, message = "Diagnosis must be between 5 and 500 characters")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis;

    @Size(max = 1000, message = "Treatment must not exceed 1000 characters")
    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Size(max = 500, message = "Medications must not exceed 500 characters")
    @Column(columnDefinition = "TEXT")
    private String medications;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be positive or zero")
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double weight;

    @DecimalMin(value = "0.0", inclusive = true, message = "Temperature must be positive or zero")
    @Column(columnDefinition = "DECIMAL(4,2)")
    private Double temperature;

    @Min(value = 0, message = "Heart rate must be positive or zero")
    @Column
    private Integer heartRate;

    @DecimalMin(value = "0.0", inclusive = true, message = "Cost must be positive or zero")
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double cost;

    private LocalDate nextVisitDate;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
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
