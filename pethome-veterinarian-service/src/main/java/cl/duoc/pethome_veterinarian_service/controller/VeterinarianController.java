package cl.duoc.pethome_veterinarian_service.controller;

import cl.duoc.pethome_veterinarian_service.dto.VeterinarianRequestDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianResponseDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianSearchDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianUpdateDTO;
import cl.duoc.pethome_veterinarian_service.service.VeterinarianService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing veterinarians.
 *
 * Provides endpoints for CRUD operations, search functionality, and availability
 * management for veterinarians in the PetHome system.
 *
 * All endpoints return standardized HTTP responses and handle validation through
 * Bean Validation annotations.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
@Tag(name = "Veterinarios", description = "Endpoints para gestión de veterinarios y perfiles profesionales")
@RestController
@RequestMapping("/veterinarians")
public class VeterinarianController {

    private static final Logger logger = LoggerFactory.getLogger(VeterinarianController.class);

    private final VeterinarianService veterinarianService;

    /**
     * Constructor for dependency injection.
     *
     * @param veterinarianService the veterinarian service
     */
    public VeterinarianController(VeterinarianService veterinarianService) {
        this.veterinarianService = veterinarianService;
    }

    /**
     * Creates a new veterinarian.
     *
     * @param request the veterinarian data to create
     * @return ResponseEntity with the created veterinarian and HTTP status 201 CREATED
     */
    @Operation(summary = "Crear veterinario", description = "Registra un nuevo veterinario con su perfil profesional")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Veterinario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "El usuario ya tiene un perfil de veterinario", content = @Content)
    })
    @PostMapping
    public ResponseEntity<VeterinarianResponseDTO> createVeterinarian(
            @Valid @RequestBody VeterinarianRequestDTO request) {
        logger.info("POST /veterinarians - Creating veterinarian for user ID: {}", request.getUserId());

        VeterinarianResponseDTO response = veterinarianService.createVeterinarian(request);

        logger.info("Veterinarian created successfully with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all veterinarians.
     *
     * @return ResponseEntity with list of all veterinarians and HTTP status 200 OK
     */
    @Operation(summary = "Obtener todos los veterinarios", description = "Retorna la lista completa de veterinarios registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    })
    @GetMapping
    public ResponseEntity<List<VeterinarianResponseDTO>> getAllVeterinarians() {
        logger.info("GET /veterinarians - Retrieving all veterinarians");

        List<VeterinarianResponseDTO> veterinarians = veterinarianService.getAllVeterinarians();

        logger.info("Retrieved {} veterinarians", veterinarians.size());
        return ResponseEntity.ok(veterinarians);
    }

    /**
     * Retrieves a veterinarian by their ID.
     *
     * @param id the veterinarian ID
     * @return ResponseEntity with the veterinarian data and HTTP status 200 OK
     */
    @Operation(summary = "Obtener veterinario por ID", description = "Retorna la información completa de un veterinario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veterinario encontrado"),
            @ApiResponse(responseCode = "404", description = "Veterinario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<VeterinarianResponseDTO> getVeterinarianById(
            @Parameter(description = "ID del veterinario") @PathVariable Long id) {
        logger.info("GET /veterinarians/{} - Retrieving veterinarian", id);

        VeterinarianResponseDTO veterinarian = veterinarianService.getVeterinarianById(id);

        logger.info("Veterinarian retrieved successfully with ID: {}", id);
        return ResponseEntity.ok(veterinarian);
    }

    /**
     * Retrieves a veterinarian by their associated user ID.
     *
     * @param userId the user ID
     * @return ResponseEntity with the veterinarian data and HTTP status 200 OK
     */
    @Operation(summary = "Obtener veterinario por User ID", description = "Busca el perfil de veterinario asociado a un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veterinario encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe veterinario para este usuario", content = @Content)
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<VeterinarianResponseDTO> getVeterinarianByUserId(
            @Parameter(description = "ID del usuario") @PathVariable Long userId) {
        logger.info("GET /veterinarians/user/{} - Retrieving veterinarian by user ID", userId);

        VeterinarianResponseDTO veterinarian = veterinarianService.getVeterinarianByUserId(userId);

        logger.info("Veterinarian retrieved successfully for user ID: {}", userId);
        return ResponseEntity.ok(veterinarian);
    }

    /**
     * Retrieves all available veterinarians.
     *
     * @return ResponseEntity with list of available veterinarians and HTTP status 200 OK
     */
    @Operation(summary = "Obtener veterinarios disponibles", description = "Retorna solo los veterinarios que están marcados como disponibles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de disponibles obtenida")
    })
    @GetMapping("/available")
    public ResponseEntity<List<VeterinarianResponseDTO>> getAvailableVeterinarians() {
        logger.info("GET /veterinarians/available - Retrieving available veterinarians");

        List<VeterinarianResponseDTO> veterinarians = veterinarianService.getAvailableVeterinarians();

        logger.info("Retrieved {} available veterinarians", veterinarians.size());
        return ResponseEntity.ok(veterinarians);
    }

    /**
     * Searches for veterinarians based on multiple criteria.
     *
     * @param search the search criteria (specialty, availability, rating)
     * @return ResponseEntity with list of matching veterinarians and HTTP status 200 OK
     */
    @Operation(summary = "Buscar veterinarios", description = "Búsqueda avanzada por especialidad, disponibilidad y calificación mínima")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente")
    })
    @PostMapping("/search")
    public ResponseEntity<List<VeterinarianResponseDTO>> searchVeterinarians(
            @Parameter(description = "Criterios de búsqueda") @RequestBody VeterinarianSearchDTO search) {
        logger.info("POST /veterinarians/search - Searching veterinarians with criteria: {}", search);

        List<VeterinarianResponseDTO> veterinarians = veterinarianService.searchVeterinarians(search);

        logger.info("Search returned {} veterinarians", veterinarians.size());
        return ResponseEntity.ok(veterinarians);
    }

    /**
     * Updates an existing veterinarian's information.
     *
     * @param id the veterinarian ID
     * @param update the data to update
     * @return ResponseEntity with the updated veterinarian and HTTP status 200 OK
     */
    @Operation(summary = "Actualizar veterinario", description = "Actualiza la información del perfil profesional de un veterinario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Veterinario actualizado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Veterinario no encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Datos inválidos", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<VeterinarianResponseDTO> updateVeterinarian(
            @Parameter(description = "ID del veterinario") @PathVariable Long id,
            @Valid @RequestBody VeterinarianUpdateDTO update) {
        logger.info("PUT /veterinarians/{} - Updating veterinarian", id);

        VeterinarianResponseDTO response = veterinarianService.updateVeterinarian(id, update);

        logger.info("Veterinarian updated successfully with ID: {}", id);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a veterinarian from the system.
     *
     * @param id the veterinarian ID
     * @return ResponseEntity with HTTP status 204 NO CONTENT
     */
    @Operation(summary = "Eliminar veterinario", description = "Elimina el perfil de veterinario de forma permanente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Veterinario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Veterinario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVeterinarian(
            @Parameter(description = "ID del veterinario") @PathVariable Long id) {
        logger.info("DELETE /veterinarians/{} - Deleting veterinarian", id);

        veterinarianService.deleteVeterinarian(id);

        logger.info("Veterinarian deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
