package cl.duoc.pethome_veterinarian_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a veterinarian in the PetHome system.
 * Contains professional information, clinic details, and availability.
 */
@Entity
@Table(name = "veterinarians")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veterinarian {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @NotBlank(message = "Specialty is required")
    @Column(nullable = false)
    private String specialty;

    @NotBlank(message = "License number is required")
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "clinic_address")
    private String clinicAddress;

    @Column(name = "working_hours")
    private String workingHours;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(nullable = false)
    @Builder.Default
    private Double rating = 0.0;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 500)
    private String photo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VeterinarianSchedule> schedules = new ArrayList<>();

    /**
     * Sets the creation timestamp before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    /**
     * Updates the modification timestamp before updating the entity.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Helper method to add a schedule to the veterinarian.
     * Maintains the bidirectional relationship.
     *
     * @param schedule the schedule to add
     */
    public void addSchedule(VeterinarianSchedule schedule) {
        schedules.add(schedule);
        schedule.setVeterinarian(this);
    }

    /**
     * Helper method to remove a schedule from the veterinarian.
     * Maintains the bidirectional relationship.
     *
     * @param schedule the schedule to remove
     */
    public void removeSchedule(VeterinarianSchedule schedule) {
        schedules.remove(schedule);
        schedule.setVeterinarian(null);
    }
}
