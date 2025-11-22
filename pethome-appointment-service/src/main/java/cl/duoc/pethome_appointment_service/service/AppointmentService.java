package cl.duoc.pethome_appointment_service.service;

import cl.duoc.pethome_appointment_service.dto.*;
import cl.duoc.pethome_appointment_service.entity.Appointment;
import cl.duoc.pethome_appointment_service.exception.AppointmentException;
import cl.duoc.pethome_appointment_service.exception.ResourceNotFoundException;
import cl.duoc.pethome_appointment_service.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for appointment business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    /**
     * Create a new appointment.
     */
    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequestDTO requestDTO) {
        log.info("Creating appointment for client: {}", requestDTO.getClientId());

        // Validate date is not in the past
        if (requestDTO.getDate().isBefore(LocalDate.now())) {
            throw new AppointmentException("Cannot create appointment in the past");
        }

        // Check if time slot is available
        boolean isBooked = appointmentRepository.isTimeSlotBooked(
                requestDTO.getVeterinarianId(),
                requestDTO.getDate(),
                requestDTO.getTime()
        );

        if (isBooked) {
            throw new AppointmentException("Time slot is already booked");
        }

        Appointment appointment = Appointment.builder()
                .clientId(requestDTO.getClientId())
                .veterinarianId(requestDTO.getVeterinarianId())
                .petId(requestDTO.getPetId())
                .serviceId(requestDTO.getServiceId())
                .date(requestDTO.getDate())
                .time(requestDTO.getTime())
                .duration(requestDTO.getDuration())
                .address(requestDTO.getAddress())
                .notes(requestDTO.getNotes())
                .status("pending")
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created with ID: {}", savedAppointment.getId());

        return convertToResponseDTO(savedAppointment);
    }

    /**
     * Get appointment by ID.
     */
    public AppointmentResponseDTO getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
        return convertToResponseDTO(appointment);
    }

    /**
     * Get all appointments.
     */
    public List<AppointmentResponseDTO> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by client ID.
     */
    public List<AppointmentResponseDTO> getAppointmentsByClientId(Long clientId) {
        return appointmentRepository.findByClientId(clientId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by veterinarian ID.
     */
    public List<AppointmentResponseDTO> getAppointmentsByVeterinarianId(Long veterinarianId) {
        return appointmentRepository.findByVeterinarianId(veterinarianId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by pet ID.
     */
    public List<AppointmentResponseDTO> getAppointmentsByPetId(Long petId) {
        return appointmentRepository.findByPetId(petId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by date.
     */
    public List<AppointmentResponseDTO> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointments by status.
     */
    public List<AppointmentResponseDTO> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get upcoming appointments for a client.
     */
    public List<AppointmentResponseDTO> getUpcomingAppointments(Long clientId) {
        return appointmentRepository.findUpcomingByClientId(clientId, LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get appointment history for a client.
     */
    public List<AppointmentResponseDTO> getAppointmentHistory(Long clientId) {
        return appointmentRepository.findHistoryByClientId(clientId, LocalDate.now()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get today's appointments for a veterinarian.
     */
    public List<AppointmentResponseDTO> getTodayAppointments(Long veterinarianId) {
        return appointmentRepository.findByVeterinarianIdAndDate(veterinarianId, LocalDate.now()).stream()
                .filter(appointment -> !appointment.getStatus().equals("cancelled"))
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update an appointment.
     */
    @Transactional
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequestDTO requestDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        // Check if appointment can be updated
        if (appointment.getStatus().equals("completed") || appointment.getStatus().equals("cancelled")) {
            throw new AppointmentException("Cannot update a completed or cancelled appointment");
        }

        // If date or time changed, check availability
        if (!appointment.getDate().equals(requestDTO.getDate()) ||
            !appointment.getTime().equals(requestDTO.getTime())) {

            boolean isBooked = appointmentRepository.isTimeSlotBooked(
                    requestDTO.getVeterinarianId(),
                    requestDTO.getDate(),
                    requestDTO.getTime()
            );

            if (isBooked) {
                throw new AppointmentException("Time slot is already booked");
            }
        }

        appointment.setDate(requestDTO.getDate());
        appointment.setTime(requestDTO.getTime());
        appointment.setDuration(requestDTO.getDuration());
        appointment.setAddress(requestDTO.getAddress());
        appointment.setNotes(requestDTO.getNotes());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment updated with ID: {}", updatedAppointment.getId());

        return convertToResponseDTO(updatedAppointment);
    }

    /**
     * Confirm an appointment.
     */
    @Transactional
    public AppointmentResponseDTO confirmAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (!appointment.getStatus().equals("pending")) {
            throw new AppointmentException("Only pending appointments can be confirmed");
        }

        appointment.setStatus("confirmed");
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment confirmed with ID: {}", id);

        return convertToResponseDTO(updatedAppointment);
    }

    /**
     * Complete an appointment with medical data.
     */
    @Transactional
    public AppointmentResponseDTO completeAppointment(Long id, CompleteAppointmentDTO completeDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus().equals("completed")) {
            throw new AppointmentException("Appointment is already completed");
        }

        if (appointment.getStatus().equals("cancelled")) {
            throw new AppointmentException("Cannot complete a cancelled appointment");
        }

        appointment.setStatus("completed");
        appointment.setDiagnosis(completeDTO.getDiagnosis());
        appointment.setPrescription(completeDTO.getPrescription());
        if (completeDTO.getNotes() != null && !completeDTO.getNotes().isEmpty()) {
            appointment.setNotes(appointment.getNotes() != null
                ? appointment.getNotes() + "\n" + completeDTO.getNotes()
                : completeDTO.getNotes());
        }
        appointment.setCompletedAt(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment completed with ID: {}", id);

        return convertToResponseDTO(updatedAppointment);
    }

    /**
     * Cancel an appointment.
     */
    @Transactional
    public AppointmentResponseDTO cancelAppointment(Long id, CancelAppointmentDTO cancelDTO) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus().equals("completed")) {
            throw new AppointmentException("Cannot cancel a completed appointment");
        }

        if (appointment.getStatus().equals("cancelled")) {
            throw new AppointmentException("Appointment is already cancelled");
        }

        appointment.setStatus("cancelled");
        appointment.setCancellationReason(cancelDTO.getReason());
        appointment.setCancelledAt(LocalDateTime.now());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment cancelled with ID: {}", id);

        return convertToResponseDTO(updatedAppointment);
    }

    /**
     * Delete an appointment (soft delete by cancelling).
     */
    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (appointment.getStatus().equals("completed")) {
            throw new AppointmentException("Cannot delete a completed appointment");
        }

        appointmentRepository.delete(appointment);
        log.info("Appointment deleted with ID: {}", id);
    }

    /**
     * Check if a time slot is available.
     */
    public boolean checkAvailability(Long veterinarianId, LocalDate date, LocalTime time) {
        return !appointmentRepository.isTimeSlotBooked(veterinarianId, date, time);
    }

    /**
     * Convert Appointment entity to ResponseDTO.
     */
    private AppointmentResponseDTO convertToResponseDTO(Appointment appointment) {
        return AppointmentResponseDTO.builder()
                .id(appointment.getId())
                .clientId(appointment.getClientId())
                .veterinarianId(appointment.getVeterinarianId())
                .petId(appointment.getPetId())
                .serviceId(appointment.getServiceId())
                .date(appointment.getDate())
                .time(appointment.getTime())
                .duration(appointment.getDuration())
                .address(appointment.getAddress())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .diagnosis(appointment.getDiagnosis())
                .prescription(appointment.getPrescription())
                .completedAt(appointment.getCompletedAt())
                .cancellationReason(appointment.getCancellationReason())
                .cancelledAt(appointment.getCancelledAt())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
