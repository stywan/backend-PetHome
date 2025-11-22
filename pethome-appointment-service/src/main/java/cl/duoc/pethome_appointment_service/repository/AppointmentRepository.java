package cl.duoc.pethome_appointment_service.repository;

import cl.duoc.pethome_appointment_service.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Repository for Appointment entity operations.
 */
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Find all appointments for a specific client.
     */
    List<Appointment> findByClientId(Long clientId);

    /**
     * Find all appointments for a specific veterinarian.
     */
    List<Appointment> findByVeterinarianId(Long veterinarianId);

    /**
     * Find all appointments for a specific pet.
     */
    List<Appointment> findByPetId(Long petId);

    /**
     * Find all appointments on a specific date.
     */
    List<Appointment> findByDate(LocalDate date);

    /**
     * Find all appointments by status.
     */
    List<Appointment> findByStatus(String status);

    /**
     * Find appointments for a client with a specific status.
     */
    List<Appointment> findByClientIdAndStatus(Long clientId, String status);

    /**
     * Find appointments for a veterinarian with a specific status.
     */
    List<Appointment> findByVeterinarianIdAndStatus(Long veterinarianId, String status);

    /**
     * Find appointments for a veterinarian on a specific date.
     */
    List<Appointment> findByVeterinarianIdAndDate(Long veterinarianId, LocalDate date);

    /**
     * Find upcoming appointments for a client (future dates, not cancelled).
     */
    @Query("SELECT a FROM Appointment a WHERE a.clientId = :clientId " +
           "AND a.date >= :currentDate AND a.status != 'cancelled' " +
           "ORDER BY a.date ASC, a.time ASC")
    List<Appointment> findUpcomingByClientId(@Param("clientId") Long clientId,
                                              @Param("currentDate") LocalDate currentDate);

    /**
     * Find past appointments for a client.
     */
    @Query("SELECT a FROM Appointment a WHERE a.clientId = :clientId " +
           "AND (a.date < :currentDate OR a.status IN ('completed', 'cancelled')) " +
           "ORDER BY a.date DESC, a.time DESC")
    List<Appointment> findHistoryByClientId(@Param("clientId") Long clientId,
                                             @Param("currentDate") LocalDate currentDate);

    /**
     * Check if a time slot is available for a veterinarian.
     */
    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.veterinarianId = :veterinarianId " +
           "AND a.date = :date AND a.time = :time AND a.status != 'cancelled'")
    boolean isTimeSlotBooked(@Param("veterinarianId") Long veterinarianId,
                             @Param("date") LocalDate date,
                             @Param("time") LocalTime time);

    /**
     * Find all appointments for a veterinarian within a date range.
     */
    @Query("SELECT a FROM Appointment a WHERE a.veterinarianId = :veterinarianId " +
           "AND a.date BETWEEN :startDate AND :endDate " +
           "ORDER BY a.date ASC, a.time ASC")
    List<Appointment> findByVeterinarianIdAndDateRange(@Param("veterinarianId") Long veterinarianId,
                                                        @Param("startDate") LocalDate startDate,
                                                        @Param("endDate") LocalDate endDate);
}
