package cl.duoc.pethome_user_service.controller;

import cl.duoc.pethome_user_service.dto.UserResponseDTO;
import cl.duoc.pethome_user_service.dto.UserUpdateDTO;
import cl.duoc.pethome_user_service.enums.UserRole;
import cl.duoc.pethome_user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for user management operations.
 * Provides CRUD operations and query endpoints for users.
 *
 * <p>This controller provides the following endpoints:</p>
 * <ul>
 *   <li>GET /users - Retrieve all users</li>
 *   <li>GET /users/{id} - Retrieve a user by ID</li>
 *   <li>GET /users/email/{email} - Retrieve a user by email</li>
 *   <li>GET /users/role/{role} - Retrieve all users with a specific role</li>
 *   <li>PUT /users/{id} - Update a user's information</li>
 *   <li>DELETE /users/{id} - Delete a user</li>
 * </ul>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Tag(name = "Usuarios", description = "Endpoints para gestión de usuarios (requiere autenticación)")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all users in the system.
     *
     * @return ResponseEntity containing a list of all users as UserResponseDTO
     */
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna la lista completa de usuarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        log.info("Retrieving all users");

        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            log.info("Retrieved {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error retrieving all users", e);
            throw e;
        }
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user's unique identifier
     * @return ResponseEntity containing the user as UserResponseDTO
     * @throws RuntimeException if the user is not found
     */
    @Operation(summary = "Obtener usuario por ID", description = "Retorna la información de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        log.info("Retrieving user with id: {}", id);

        try {
            UserResponseDTO user = userService.getUserById(id);
            log.info("User retrieved successfully: {}", id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error retrieving user with id: {}", id, e);
            throw e;
        }
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the user's email address
     * @return ResponseEntity containing the user as UserResponseDTO
     * @throws RuntimeException if the user is not found
     */
    @Operation(summary = "Obtener usuario por email", description = "Busca un usuario por su dirección de correo electrónico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(
            @Parameter(description = "Email del usuario") @PathVariable String email) {
        log.info("Retrieving user with email: {}", email);

        try {
            UserResponseDTO user = userService.getUserByEmail(email);
            log.info("User retrieved successfully by email: {}", email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            log.error("Error retrieving user with email: {}", email, e);
            throw e;
        }
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param role the user role to filter by (CLIENT, VET, or ADMIN)
     * @return ResponseEntity containing a list of users with the specified role
     */
    @Operation(summary = "Obtener usuarios por rol", description = "Filtra usuarios por su rol. Valores: CLIENT, VET, ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida"),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(
            @Parameter(description = "Rol del usuario (CLIENT, VET, ADMIN)") @PathVariable UserRole role) {
        log.info("Retrieving users with role: {}", role);

        try {
            List<UserResponseDTO> users = userService.getUsersByRole(role);
            log.info("Retrieved {} users with role: {}", users.size(), role);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error retrieving users with role: {}", role, e);
            throw e;
        }
    }

    /**
     * Updates an existing user's information.
     * Only the fields provided in the update DTO will be modified.
     *
     * @param id     the user's unique identifier
     * @param update the update DTO containing fields to update
     * @return ResponseEntity containing the updated user as UserResponseDTO
     * @throws RuntimeException if the user is not found
     */
    @Operation(summary = "Actualizar usuario", description = "Actualiza la información de un usuario existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO update) {
        log.info("Updating user with id: {}", id);

        try {
            UserResponseDTO updatedUser = userService.updateUser(id, update);
            log.info("User updated successfully: {}", id);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            log.error("Error updating user with id: {}", id, e);
            throw e;
        }
    }

    /**
     * Deletes a user from the system.
     *
     * @param id the user's unique identifier
     * @return ResponseEntity with no content (204)
     * @throws RuntimeException if the user is not found
     */
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema de forma permanente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        log.info("Deleting user with id: {}", id);

        try {
            userService.deleteUser(id);
            log.info("User deleted successfully: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting user with id: {}", id, e);
            throw e;
        }
    }
}
