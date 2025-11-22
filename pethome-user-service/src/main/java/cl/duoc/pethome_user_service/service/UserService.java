package cl.duoc.pethome_user_service.service;

import cl.duoc.pethome_user_service.dto.UserRequestDTO;
import cl.duoc.pethome_user_service.dto.UserResponseDTO;
import cl.duoc.pethome_user_service.dto.UserUpdateDTO;
import cl.duoc.pethome_user_service.enums.UserRole;

import java.util.List;

/**
 * Service interface for User management operations.
 * Provides business logic methods for user CRUD operations and queries.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
public interface UserService {

    /**
     * Creates a new user in the system.
     *
     * @param request the user creation request containing user details
     * @return the created user as a response DTO
     * @throws RuntimeException if the email already exists
     */
    UserResponseDTO createUser(UserRequestDTO request);

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user's unique identifier
     * @return the user as a response DTO
     * @throws RuntimeException if the user is not found
     */
    UserResponseDTO getUserById(Long id);

    /**
     * Retrieves a user by their email address.
     *
     * @param email the user's email address
     * @return the user as a response DTO
     * @throws RuntimeException if the user is not found
     */
    UserResponseDTO getUserByEmail(String email);

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users as response DTOs
     */
    List<UserResponseDTO> getAllUsers();

    /**
     * Retrieves all users with a specific role.
     *
     * @param role the user role to filter by
     * @return a list of users with the specified role
     */
    List<UserResponseDTO> getUsersByRole(UserRole role);

    /**
     * Updates an existing user's information.
     *
     * @param id     the user's unique identifier
     * @param update the update DTO containing fields to update
     * @return the updated user as a response DTO
     * @throws RuntimeException if the user is not found
     */
    UserResponseDTO updateUser(Long id, UserUpdateDTO update);

    /**
     * Deletes a user from the system.
     *
     * @param id the user's unique identifier
     * @throws RuntimeException if the user is not found
     */
    void deleteUser(Long id);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
}
