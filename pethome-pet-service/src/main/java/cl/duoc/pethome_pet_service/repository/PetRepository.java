package cl.duoc.pethome_pet_service.repository;

import cl.duoc.pethome_pet_service.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Pet entity operations.
 * Provides methods for querying and manipulating pet data.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Finds all pets belonging to a specific owner.
     *
     * @param ownerId The owner's ID
     * @return List of pets belonging to the owner
     */
    List<Pet> findByOwnerId(Long ownerId);

    /**
     * Finds all active pets belonging to a specific owner.
     *
     * @param ownerId  The owner's ID
     * @param isActive Active status
     * @return List of active pets belonging to the owner
     */
    List<Pet> findByOwnerIdAndIsActive(Long ownerId, Boolean isActive);

    /**
     * Finds all pets by species.
     *
     * @param species The species to search for
     * @return List of pets of the specified species
     */
    List<Pet> findBySpeciesIgnoreCase(String species);

    /**
     * Finds all pets by species and breed.
     *
     * @param species The species to search for
     * @param breed   The breed to search for
     * @return List of pets matching species and breed
     */
    List<Pet> findBySpeciesIgnoreCaseAndBreedIgnoreCase(String species, String breed);

    /**
     * Finds pets by name containing a search term (case-insensitive).
     *
     * @param name The name search term
     * @return List of pets with names containing the search term
     */
    List<Pet> findByNameContainingIgnoreCase(String name);

    /**
     * Finds all active pets.
     *
     * @return List of active pets
     */
    List<Pet> findByIsActiveTrue();

    /**
     * Checks if a pet exists for a given owner.
     *
     * @param ownerId The owner's ID
     * @return true if at least one pet exists for the owner
     */
    boolean existsByOwnerId(Long ownerId);

    /**
     * Counts the number of pets belonging to an owner.
     *
     * @param ownerId The owner's ID
     * @return Number of pets
     */
    long countByOwnerId(Long ownerId);

    /**
     * Finds pets by owner ID and species.
     *
     * @param ownerId The owner's ID
     * @param species The species to filter by
     * @return List of pets matching the criteria
     */
    @Query("SELECT p FROM Pet p WHERE p.ownerId = :ownerId AND LOWER(p.species) = LOWER(:species)")
    List<Pet> findPetsByOwnerAndSpecies(@Param("ownerId") Long ownerId, @Param("species") String species);

    /**
     * Finds a pet by ID and owner ID for security purposes.
     *
     * @param id      The pet's ID
     * @param ownerId The owner's ID
     * @return Optional containing the pet if found
     */
    Optional<Pet> findByIdAndOwnerId(Long id, Long ownerId);
}
