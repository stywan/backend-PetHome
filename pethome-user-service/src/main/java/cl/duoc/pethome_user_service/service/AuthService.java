package cl.duoc.pethome_user_service.service;

import cl.duoc.pethome_user_service.dto.LoginRequestDTO;
import cl.duoc.pethome_user_service.dto.LoginResponseDTO;
import cl.duoc.pethome_user_service.dto.UserRequestDTO;
import cl.duoc.pethome_user_service.dto.UserResponseDTO;

/**
 * Service interface for Authentication operations.
 * Provides methods for user login and registration with JWT token generation.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
public interface AuthService {

    /**
     * Authenticates a user with email and password.
     * Validates credentials and generates a JWT token upon successful authentication.
     *
     * @param request the login request containing email and password
     * @return the login response containing JWT token and user information
     * @throws RuntimeException if credentials are invalid
     */
    LoginResponseDTO login(LoginRequestDTO request);

    /**
     * Registers a new user in the system.
     * Creates a user account with encrypted password and default role.
     *
     * @param request the user registration request containing user details
     * @return the created user as a response DTO
     * @throws RuntimeException if the email already exists
     */
    UserResponseDTO register(UserRequestDTO request);
}
