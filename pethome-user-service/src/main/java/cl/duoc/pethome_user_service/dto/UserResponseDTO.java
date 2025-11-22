package cl.duoc.pethome_user_service.dto;

import cl.duoc.pethome_user_service.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for User response (without sensitive information like password).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {

    private Long id;

    private String name;

    private String email;

    private String phone;

    private UserRole role;

    private String photo;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AddressDTO> addresses;
}
