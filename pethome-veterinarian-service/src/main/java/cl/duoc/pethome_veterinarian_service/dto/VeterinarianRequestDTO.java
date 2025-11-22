package cl.duoc.pethome_veterinarian_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarianRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "La especialidad es obligatoria")
    private String specialty;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String licenseNumber;

    private String clinicName;
    private String clinicAddress;
    private String workingHours;
    private String bio;

    @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
    private Integer experienceYears;

    private String photo;
}
