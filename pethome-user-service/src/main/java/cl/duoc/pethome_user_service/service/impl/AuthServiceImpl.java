package cl.duoc.pethome_user_service.service.impl;

import cl.duoc.pethome_user_service.dto.LoginRequestDTO;
import cl.duoc.pethome_user_service.dto.LoginResponseDTO;
import cl.duoc.pethome_user_service.dto.UserRequestDTO;
import cl.duoc.pethome_user_service.dto.UserResponseDTO;
import cl.duoc.pethome_user_service.entity.User;
import cl.duoc.pethome_user_service.repository.UserRepository;
import cl.duoc.pethome_user_service.service.AuthService;
import cl.duoc.pethome_user_service.service.UserService;
import cl.duoc.pethome_user_service.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the AuthService interface.
 * Provides authentication and registration services including credential validation,
 * JWT token generation, and user account creation.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with dependency injection.
     *
     * @param userService     the user service for user operations
     * @param userRepository  the user repository for direct database access
     * @param jwtUtil         the JWT utility for token generation
     * @param passwordEncoder the password encoder for password validation
     */
    public AuthServiceImpl(UserService userService,
                           UserRepository userRepository,
                           JwtUtil jwtUtil,
                           PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user with email and password.
     * Validates credentials against the database and generates a JWT token
     * upon successful authentication.
     *
     * @param request the login request containing email and password
     * @return the login response containing JWT token and user information
     * @throws RuntimeException if credentials are invalid or user not found
     */
    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO request) {
        logger.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", request.getEmail());
                    return new RuntimeException("Invalid email or password");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Invalid password for email: {}", request.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        logger.info("User authenticated successfully: {}", user.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        logger.debug("JWT token generated for user: {}", user.getEmail());

        // Get user details
        UserResponseDTO userResponse = userService.getUserByEmail(user.getEmail());

        // Build login response
        LoginResponseDTO response = LoginResponseDTO.builder()
                .token(token)
                .type("Bearer")
                .user(userResponse)
                .build();

        logger.info("Login successful for user: {}", user.getEmail());
        return response;
    }

    /**
     * Registers a new user in the system.
     * Validates that the email is unique, creates a user account with
     * encrypted password, and assigns a default role if not provided.
     *
     * @param request the user registration request containing user details
     * @return the created user as a response DTO
     * @throws RuntimeException if the email already exists
     */
    @Override
    public UserResponseDTO register(UserRequestDTO request) {
        logger.info("Registration attempt for email: {}", request.getEmail());

        // Validate email uniqueness
        if (userService.existsByEmail(request.getEmail())) {
            logger.error("Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Validate password presence
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            logger.error("Password is required for registration");
            throw new RuntimeException("Password is required");
        }

        logger.debug("Creating new user account for: {}", request.getEmail());

        // Create user through UserService (password will be encrypted there)
        UserResponseDTO createdUser = userService.createUser(request);

        logger.info("User registered successfully with email: {}", request.getEmail());
        return createdUser;
    }
}
