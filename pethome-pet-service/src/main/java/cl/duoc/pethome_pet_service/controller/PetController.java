package cl.duoc.pethome_pet_service.controller;

import cl.duoc.pethome_pet_service.dto.*;
import cl.duoc.pethome_pet_service.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for pet-related endpoints.
 * Handles HTTP requests for pet and medical record operations.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Tag(name = "Mascotas", description = "Endpoints para gestión de mascotas y fichas médicas")
@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
@Slf4j
public class PetController {

    private final PetService petService;

    /**
     * Creates a new pet.
     *
     * @param petRequestDTO Pet data
     * @return Created pet
     */
    @Operation(summary = "Crear mascota", description = "Registra una nueva mascota en el sistema")
    @PostMapping
    public ResponseEntity<PetResponseDTO> createPet(@Valid @RequestBody PetRequestDTO petRequestDTO) {
        log.info("POST /pets - Creating new pet");
        PetResponseDTO response = petService.createPet(petRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all pets.
     *
     * @return List of all pets
     */
    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> getAllPets() {
        log.info("GET /pets - Fetching all pets");
        List<PetResponseDTO> pets = petService.getAllPets();
        return ResponseEntity.ok(pets);
    }

    /**
     * Retrieves a pet by ID.
     *
     * @param id Pet ID
     * @return Pet data
     */
    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> getPetById(@PathVariable Long id) {
        log.info("GET /pets/{} - Fetching pet by ID", id);
        PetResponseDTO pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    /**
     * Retrieves all pets for a specific owner.
     *
     * @param ownerId Owner's ID
     * @return List of owner's pets
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PetResponseDTO>> getPetsByOwnerId(@PathVariable Long ownerId) {
        log.info("GET /pets/owner/{} - Fetching pets by owner ID", ownerId);
        List<PetResponseDTO> pets = petService.getPetsByOwnerId(ownerId);
        return ResponseEntity.ok(pets);
    }

    /**
     * Retrieves active pets for a specific owner.
     *
     * @param ownerId Owner's ID
     * @return List of active pets
     */
    @GetMapping("/owner/{ownerId}/active")
    public ResponseEntity<List<PetResponseDTO>> getActivePetsByOwnerId(@PathVariable Long ownerId) {
        log.info("GET /pets/owner/{}/active - Fetching active pets by owner ID", ownerId);
        List<PetResponseDTO> pets = petService.getActivePetsByOwnerId(ownerId);
        return ResponseEntity.ok(pets);
    }

    /**
     * Retrieves pets by species.
     *
     * @param species Species name
     * @return List of pets
     */
    @GetMapping("/species/{species}")
    public ResponseEntity<List<PetResponseDTO>> getPetsBySpecies(@PathVariable String species) {
        log.info("GET /pets/species/{} - Fetching pets by species", species);
        List<PetResponseDTO> pets = petService.getPetsBySpecies(species);
        return ResponseEntity.ok(pets);
    }

    /**
     * Searches pets by name.
     *
     * @param name Name search term
     * @return List of matching pets
     */
    @GetMapping("/search")
    public ResponseEntity<List<PetResponseDTO>> searchPetsByName(@RequestParam String name) {
        log.info("GET /pets/search?name={} - Searching pets by name", name);
        List<PetResponseDTO> pets = petService.searchPetsByName(name);
        return ResponseEntity.ok(pets);
    }

    /**
     * Updates a pet.
     *
     * @param id           Pet ID
     * @param petUpdateDTO Updated pet data
     * @return Updated pet
     */
    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> updatePet(
            @PathVariable Long id,
            @Valid @RequestBody PetUpdateDTO petUpdateDTO) {
        log.info("PUT /pets/{} - Updating pet", id);
        PetResponseDTO updatedPet = petService.updatePet(id, petUpdateDTO);
        return ResponseEntity.ok(updatedPet);
    }

    /**
     * Deletes a pet.
     *
     * @param id Pet ID
     * @return No content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Long id) {
        log.info("DELETE /pets/{} - Deleting pet", id);
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Adds a medical record for a pet.
     *
     * @param medicalRecordRequestDTO Medical record data
     * @return Created medical record
     */
    @PostMapping("/medical-records")
    public ResponseEntity<MedicalRecordResponseDTO> addMedicalRecord(
            @Valid @RequestBody MedicalRecordRequestDTO medicalRecordRequestDTO) {
        log.info("POST /pets/medical-records - Creating medical record for pet ID: {}",
                medicalRecordRequestDTO.getPetId());
        MedicalRecordResponseDTO response = petService.addMedicalRecord(medicalRecordRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves all medical records for a pet.
     *
     * @param petId Pet ID
     * @return List of medical records
     */
    @GetMapping("/{petId}/medical-records")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByPetId(@PathVariable Long petId) {
        log.info("GET /pets/{}/medical-records - Fetching medical records", petId);
        List<MedicalRecordResponseDTO> records = petService.getMedicalRecordsByPetId(petId);
        return ResponseEntity.ok(records);
    }

    /**
     * Retrieves a medical record by ID.
     *
     * @param id Medical record ID
     * @return Medical record data
     */
    @GetMapping("/medical-records/{id}")
    public ResponseEntity<MedicalRecordResponseDTO> getMedicalRecordById(@PathVariable Long id) {
        log.info("GET /pets/medical-records/{} - Fetching medical record by ID", id);
        MedicalRecordResponseDTO record = petService.getMedicalRecordById(id);
        return ResponseEntity.ok(record);
    }

    /**
     * Retrieves medical records by veterinarian.
     *
     * @param veterinarianId Veterinarian's ID
     * @return List of medical records
     */
    @GetMapping("/medical-records/veterinarian/{veterinarianId}")
    public ResponseEntity<List<MedicalRecordResponseDTO>> getMedicalRecordsByVeterinarianId(
            @PathVariable Long veterinarianId) {
        log.info("GET /pets/medical-records/veterinarian/{} - Fetching medical records by veterinarian", veterinarianId);
        List<MedicalRecordResponseDTO> records = petService.getMedicalRecordsByVeterinarianId(veterinarianId);
        return ResponseEntity.ok(records);
    }

    /**
     * Deletes a medical record.
     *
     * @param id Medical record ID
     * @return No content
     */
    @DeleteMapping("/medical-records/{id}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable Long id) {
        log.info("DELETE /pets/medical-records/{} - Deleting medical record", id);
        petService.deleteMedicalRecord(id);
        return ResponseEntity.noContent().build();
    }
}
