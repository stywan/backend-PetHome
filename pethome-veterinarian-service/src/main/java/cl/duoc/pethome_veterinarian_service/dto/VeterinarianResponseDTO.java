package cl.duoc.pethome_veterinarian_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarianResponseDTO {

    private Long id;
    private Long userId;
    private String specialty;
    private String licenseNumber;
    private String clinicName;
    private String clinicAddress;
    private String workingHours;
    private String bio;
    private Boolean isAvailable;
    private Double rating;
    private Integer experienceYears;
    private String photo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<VeterinarianScheduleDTO> schedules;
}
