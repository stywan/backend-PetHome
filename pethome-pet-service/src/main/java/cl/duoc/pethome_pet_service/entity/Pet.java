package cl.duoc.pethome_pet_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a pet in the PetHome system.
 * Contains pet information and medical history.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Pet name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String species;

    @Size(max = 50, message = "Breed must not exceed 50 characters")
    @Column(length = 50)
    private String breed;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @Column(nullable = false)
    private LocalDate birthDate;

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(MALE|FEMALE|UNKNOWN)$", message = "Gender must be MALE, FEMALE, or UNKNOWN")
    @Column(nullable = false, length = 10)
    private String gender;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    @Column(length = 50)
    private String color;

    @Positive(message = "Weight must be positive")
    @Column(columnDefinition = "DECIMAL(5,2)")
    private Double weight;

    @Column(columnDefinition = "TEXT")
    private String medicalConditions;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @NotNull(message = "Owner ID is required")
    @Column(nullable = false)
    private Long ownerId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isActive = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MedicalRecord> medicalRecords = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the age of the pet in years.
     *
     * @return Age in years
     */
    @Transient
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Gets a formatted age string (e.g., "2 years", "6 months").
     *
     * @return Formatted age string
     */
    @Transient
    public String getFormattedAge() {
        if (birthDate == null) {
            return "Unknown";
        }
        Period period = Period.between(birthDate, LocalDate.now());
        int years = period.getYears();
        int months = period.getMonths();

        if (years > 0) {
            return years + (years == 1 ? " year" : " years");
        } else if (months > 0) {
            return months + (months == 1 ? " month" : " months");
        } else {
            return "Less than a month";
        }
    }
}
