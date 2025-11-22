package cl.duoc.pethome_veterinarian_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeterinarianSearchDTO {

    private String specialty;
    private Boolean isAvailable;
    private Double minRating;
}
