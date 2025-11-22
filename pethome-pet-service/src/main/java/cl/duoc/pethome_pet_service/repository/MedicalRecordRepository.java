package cl.duoc.pethome_pet_service.repository;

import cl.duoc.pethome_pet_service.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for MedicalRecord entity operations.
 * Provides methods for querying and manipulating medical record data.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    /**
     * Finds all medical records for a specific pet.
     *
     * @param petId The pet's ID
     * @return List of medical records for the pet
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetId(@Param("petId") Long petId);

    /**
     * Finds all medical records for a specific veterinarian.
     *
     * @param veterinarianId The veterinarian's ID
     * @return List of medical records created by the veterinarian
     */
    List<MedicalRecord> findByVeterinarianIdOrderByVisitDateDesc(Long veterinarianId);

    /**
     * Finds medical records by type for a specific pet.
     *
     * @param petId      The pet's ID
     * @param recordType The record type
     * @return List of medical records matching the criteria
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId AND mr.recordType = :recordType ORDER BY mr.visitDate DESC")
    List<MedicalRecord> findByPetIdAndRecordType(@Param("petId") Long petId, @Param("recordType") String recordType);

    /**
     * Finds medical records within a date range.
     *
     * @param startDate Start date of the range
     * @param endDate   End date of the range
     * @return List of medical records within the date range
     */
    List<MedicalRecord> findByVisitDateBetweenOrderByVisitDateDesc(LocalDate startDate, LocalDate endDate);

    /**
     * Finds records with upcoming next visit dates.
     *
     * @param currentDate Current date
     * @return List of medical records with future next visit dates
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.nextVisitDate >= :currentDate ORDER BY mr.nextVisitDate ASC")
    List<MedicalRecord> findUpcomingVisits(@Param("currentDate") LocalDate currentDate);

    /**
     * Finds the most recent medical record for a pet.
     *
     * @param petId The pet's ID
     * @return The most recent medical record
     */
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.pet.id = :petId ORDER BY mr.visitDate DESC LIMIT 1")
    MedicalRecord findLatestByPetId(@Param("petId") Long petId);

    /**
     * Counts medical records for a specific pet.
     *
     * @param petId The pet's ID
     * @return Number of medical records
     */
    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.pet.id = :petId")
    long countByPetId(@Param("petId") Long petId);

    /**
     * Finds medical records by record type.
     *
     * @param recordType The record type
     * @return List of medical records of the specified type
     */
    List<MedicalRecord> findByRecordTypeOrderByVisitDateDesc(String recordType);
}
