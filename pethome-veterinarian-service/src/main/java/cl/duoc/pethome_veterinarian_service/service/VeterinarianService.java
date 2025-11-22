package cl.duoc.pethome_veterinarian_service.service;

import cl.duoc.pethome_veterinarian_service.dto.VeterinarianRequestDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianResponseDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianSearchDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianUpdateDTO;

import java.util.List;

/**
 * Service interface for veterinarian business logic.
 *
 * Defines the contract for veterinarian-related operations including
 * CRUD operations, search functionality, and availability management.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
public interface VeterinarianService {

    /**
     * Creates a new veterinarian in the system.
     *
     * @param request the veterinarian data to create
     * @return the created veterinarian data
     * @throws IllegalArgumentException if the user ID or license number already exists
     */
    VeterinarianResponseDTO createVeterinarian(VeterinarianRequestDTO request);

    /**
     * Retrieves a veterinarian by their ID.
     *
     * @param id the veterinarian ID
     * @return the veterinarian data
     * @throws cl.duoc.pethome_veterinarian_service.exception.ResourceNotFoundException if veterinarian not found
     */
    VeterinarianResponseDTO getVeterinarianById(Long id);

    /**
     * Retrieves a veterinarian by their associated user ID.
     *
     * @param userId the user ID
     * @return the veterinarian data
     * @throws cl.duoc.pethome_veterinarian_service.exception.ResourceNotFoundException if veterinarian not found
     */
    VeterinarianResponseDTO getVeterinarianByUserId(Long userId);

    /**
     * Retrieves all veterinarians in the system.
     *
     * @return list of all veterinarians
     */
    List<VeterinarianResponseDTO> getAllVeterinarians();

    /**
     * Retrieves all available veterinarians.
     *
     * @return list of available veterinarians
     */
    List<VeterinarianResponseDTO> getAvailableVeterinarians();

    /**
     * Searches for veterinarians based on multiple criteria.
     *
     * @param search the search criteria (specialty, availability, rating)
     * @return list of veterinarians matching the criteria
     */
    List<VeterinarianResponseDTO> searchVeterinarians(VeterinarianSearchDTO search);

    /**
     * Updates an existing veterinarian's information.
     *
     * @param id the veterinarian ID
     * @param update the data to update
     * @return the updated veterinarian data
     * @throws cl.duoc.pethome_veterinarian_service.exception.ResourceNotFoundException if veterinarian not found
     */
    VeterinarianResponseDTO updateVeterinarian(Long id, VeterinarianUpdateDTO update);

    /**
     * Deletes a veterinarian from the system.
     *
     * @param id the veterinarian ID
     * @throws cl.duoc.pethome_veterinarian_service.exception.ResourceNotFoundException if veterinarian not found
     */
    void deleteVeterinarian(Long id);
}
