package cl.duoc.pethome_veterinarian_service.repository;

import cl.duoc.pethome_veterinarian_service.entity.Veterinarian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Veterinarian entity.
 *
 * Provides CRUD operations and custom query methods for managing veterinarians
 * in the PetHome system. Extends JpaRepository to inherit standard data access methods.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
@Repository
public interface VeterinarianRepository extends JpaRepository<Veterinarian, Long> {

    /**
     * Finds a veterinarian by their associated user ID.
     *
     * @param userId the ID of the user associated with the veterinarian
     * @return Optional containing the veterinarian if found, empty otherwise
     */
    Optional<Veterinarian> findByUserId(Long userId);

    /**
     * Finds a veterinarian by their license number.
     *
     * @param licenseNumber the unique license number of the veterinarian
     * @return Optional containing the veterinarian if found, empty otherwise
     */
    Optional<Veterinarian> findByLicenseNumber(String licenseNumber);

    /**
     * Finds all veterinarians whose specialty contains the specified text (case-insensitive).
     *
     * @param specialty the specialty text to search for
     * @return List of veterinarians matching the specialty criteria
     */
    List<Veterinarian> findBySpecialtyContainingIgnoreCase(String specialty);

    /**
     * Finds all veterinarians who are currently available.
     *
     * @return List of available veterinarians
     */
    List<Veterinarian> findByIsAvailableTrue();

    /**
     * Finds all veterinarians with a rating greater than or equal to the specified value.
     *
     * @param rating the minimum rating threshold
     * @return List of veterinarians meeting the rating criteria
     */
    List<Veterinarian> findByRatingGreaterThanEqual(Double rating);

    /**
     * Checks if a veterinarian exists for the given user ID.
     *
     * @param userId the ID of the user to check
     * @return true if a veterinarian exists for the user, false otherwise
     */
    boolean existsByUserId(Long userId);

    /**
     * Checks if a veterinarian exists with the given license number.
     *
     * @param licenseNumber the license number to check
     * @return true if a veterinarian exists with the license number, false otherwise
     */
    boolean existsByLicenseNumber(String licenseNumber);
}
