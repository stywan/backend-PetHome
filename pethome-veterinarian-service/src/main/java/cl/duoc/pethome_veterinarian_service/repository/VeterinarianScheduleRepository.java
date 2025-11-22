package cl.duoc.pethome_veterinarian_service.repository;

import cl.duoc.pethome_veterinarian_service.entity.VeterinarianSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for VeterinarianSchedule entity.
 *
 * Provides CRUD operations and custom query methods for managing veterinarian schedules
 * in the PetHome system. Extends JpaRepository to inherit standard data access methods.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
@Repository
public interface VeterinarianScheduleRepository extends JpaRepository<VeterinarianSchedule, Long> {

    /**
     * Finds all schedules for a specific veterinarian.
     *
     * @param veterinarianId the ID of the veterinarian
     * @return List of schedules for the veterinarian
     */
    List<VeterinarianSchedule> findByVeterinarianId(Long veterinarianId);

    /**
     * Finds all active schedules for a specific veterinarian.
     *
     * @param veterinarianId the ID of the veterinarian
     * @return List of active schedules for the veterinarian
     */
    List<VeterinarianSchedule> findByVeterinarianIdAndIsActiveTrue(Long veterinarianId);
}
