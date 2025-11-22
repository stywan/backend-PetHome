package cl.duoc.pethome_user_service.controller;

import cl.duoc.pethome_user_service.dto.LoginRequestDTO;
import cl.duoc.pethome_user_service.dto.LoginResponseDTO;
import cl.duoc.pethome_user_service.dto.UserRequestDTO;
import cl.duoc.pethome_user_service.dto.UserResponseDTO;
import cl.duoc.pethome_user_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for authentication operations.
 * Handles user login and registration endpoints.
 *
 * <p>This controller provides the following endpoints:</p>
 * <ul>
 *   <li>POST /auth/login - Authenticate a user and generate JWT token</li>
 *   <li>POST /auth/register - Register a new user account</li>
 * </ul>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro de usuarios")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user with email and password.
     * Validates credentials and returns a JWT token upon successful authentication.
     *
     * @param request the login request containing email and password
     * @return ResponseEntity containing LoginResponseDTO with JWT token and user information
     * @throws RuntimeException if credentials are invalid
     */
    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica un usuario con email y contraseña, retorna un token JWT válido por 24 horas"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login exitoso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Login attempt for email: {}", request.getEmail());

        try {
            LoginResponseDTO response = authService.login(request);
            log.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }

    /**
     * Registers a new user in the system.
     * Creates a user account with encrypted password and returns user information.
     *
     * @param request the user registration request containing user details
     * @return ResponseEntity containing UserResponseDTO with created user information
     * @throws RuntimeException if the email already exists
     */
    @Operation(
            summary = "Registrar nuevo usuario",
            description = "Crea una nueva cuenta de usuario con contraseña encriptada. Los roles disponibles son: CLIENT, VET, ADMIN"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuario registrado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDTO.class))
            ),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        try {
            UserResponseDTO response = authService.register(request);
            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            throw e;
        }
    }
}
