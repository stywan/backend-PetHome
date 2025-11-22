package cl.duoc.pethome_pet_service.service;

import cl.duoc.pethome_pet_service.dto.*;

import java.util.List;

/**
 * Service interface for pet-related operations.
 * Defines business logic for managing pets and their medical records.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
public interface PetService {

    /**
     * Creates a new pet.
     *
     * @param petRequestDTO Pet data
     * @return Created pet
     */
    PetResponseDTO createPet(PetRequestDTO petRequestDTO);

    /**
     * Retrieves all pets.
     *
     * @return List of all pets
     */
    List<PetResponseDTO> getAllPets();

    /**
     * Retrieves a pet by ID.
     *
     * @param id Pet ID
     * @return Pet data
     */
    PetResponseDTO getPetById(Long id);

    /**
     * Retrieves all pets belonging to an owner.
     *
     * @param ownerId Owner's ID
     * @return List of owner's pets
     */
    List<PetResponseDTO> getPetsByOwnerId(Long ownerId);

    /**
     * Retrieves active pets for an owner.
     *
     * @param ownerId Owner's ID
     * @return List of active pets
     */
    List<PetResponseDTO> getActivePetsByOwnerId(Long ownerId);

    /**
     * Retrieves pets by species.
     *
     * @param species Species name
     * @return List of pets of the specified species
     */
    List<PetResponseDTO> getPetsBySpecies(String species);

    /**
     * Searches pets by name.
     *
     * @param name Name search term
     * @return List of pets matching the search
     */
    List<PetResponseDTO> searchPetsByName(String name);

    /**
     * Updates pet information.
     *
     * @param id            Pet ID
     * @param petUpdateDTO  Updated pet data
     * @return Updated pet
     */
    PetResponseDTO updatePet(Long id, PetUpdateDTO petUpdateDTO);

    /**
     * Deletes a pet.
     *
     * @param id Pet ID
     */
    void deletePet(Long id);

    /**
     * Adds a medical record for a pet.
     *
     * @param medicalRecordRequestDTO Medical record data
     * @return Created medical record
     */
    MedicalRecordResponseDTO addMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO);

    /**
     * Retrieves all medical records for a pet.
     *
     * @param petId Pet ID
     * @return List of medical records
     */
    List<MedicalRecordResponseDTO> getMedicalRecordsByPetId(Long petId);

    /**
     * Retrieves a medical record by ID.
     *
     * @param id Medical record ID
     * @return Medical record data
     */
    MedicalRecordResponseDTO getMedicalRecordById(Long id);

    /**
     * Retrieves medical records by veterinarian.
     *
     * @param veterinarianId Veterinarian's ID
     * @return List of medical records
     */
    List<MedicalRecordResponseDTO> getMedicalRecordsByVeterinarianId(Long veterinarianId);

    /**
     * Deletes a medical record.
     *
     * @param id Medical record ID
     */
    void deleteMedicalRecord(Long id);
}
