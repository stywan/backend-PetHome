package cl.duoc.pethome_veterinarian_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarianUpdateDTO {

    private String specialty;
    private String clinicName;
    private String clinicAddress;
    private String workingHours;
    private String bio;
    private Boolean isAvailable;
    private Integer experienceYears;
    private String photo;
}
