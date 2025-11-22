package cl.duoc.pethome_appointment_service.controller;

import cl.duoc.pethome_appointment_service.dto.*;
import cl.duoc.pethome_appointment_service.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * REST controller for appointment endpoints.
 */
@Tag(name = "Citas", description = "Endpoints para gestión de citas veterinarias")
@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
public class AppointmentController {

    private final AppointmentService appointmentService;

    /**
     * Create a new appointment.
     */
    @Operation(summary = "Crear cita", description = "Agenda una nueva cita veterinaria")
    @PostMapping
    public ResponseEntity<AppointmentResponseDTO> createAppointment(
            @Valid @RequestBody AppointmentRequestDTO requestDTO) {
        log.info("POST /appointments - Creating appointment for client: {}", requestDTO.getClientId());
        AppointmentResponseDTO response = appointmentService.createAppointment(requestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all appointments.
     */
    @GetMapping
    public ResponseEntity<List<AppointmentResponseDTO>> getAllAppointments() {
        log.info("GET /appointments - Fetching all appointments");
        List<AppointmentResponseDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointment by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable Long id) {
        log.info("GET /appointments/{} - Fetching appointment", id);
        AppointmentResponseDTO appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    /**
     * Update an appointment.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequestDTO requestDTO) {
        log.info("PUT /appointments/{} - Updating appointment", id);
        AppointmentResponseDTO response = appointmentService.updateAppointment(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete an appointment.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAppointment(@PathVariable Long id) {
        log.info("DELETE /appointments/{} - Deleting appointment", id);
        appointmentService.deleteAppointment(id);
        return ResponseEntity.ok(Map.of("message", "Appointment deleted successfully"));
    }

    /**
     * Get appointments by client ID.
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByClientId(
            @PathVariable Long clientId) {
        log.info("GET /appointments/client/{} - Fetching appointments", clientId);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByClientId(clientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments by veterinarian ID.
     */
    @GetMapping("/vet/{veterinarianId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByVeterinarianId(
            @PathVariable Long veterinarianId) {
        log.info("GET /appointments/vet/{} - Fetching appointments", veterinarianId);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByVeterinarianId(veterinarianId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments by pet ID.
     */
    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByPetId(
            @PathVariable Long petId) {
        log.info("GET /appointments/pet/{} - Fetching appointments", petId);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByPetId(petId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments by date.
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("GET /appointments/date/{} - Fetching appointments", date);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByDate(date);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointments by status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentsByStatus(
            @PathVariable String status) {
        log.info("GET /appointments/status/{} - Fetching appointments", status);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentsByStatus(status);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get upcoming appointments for a client.
     */
    @GetMapping("/client/{clientId}/upcoming")
    public ResponseEntity<List<AppointmentResponseDTO>> getUpcomingAppointments(
            @PathVariable Long clientId) {
        log.info("GET /appointments/client/{}/upcoming - Fetching upcoming appointments", clientId);
        List<AppointmentResponseDTO> appointments = appointmentService.getUpcomingAppointments(clientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get appointment history for a client.
     */
    @GetMapping("/client/{clientId}/history")
    public ResponseEntity<List<AppointmentResponseDTO>> getAppointmentHistory(
            @PathVariable Long clientId) {
        log.info("GET /appointments/client/{}/history - Fetching appointment history", clientId);
        List<AppointmentResponseDTO> appointments = appointmentService.getAppointmentHistory(clientId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Get today's appointments for a veterinarian.
     */
    @GetMapping("/vet/{veterinarianId}/today")
    public ResponseEntity<List<AppointmentResponseDTO>> getTodayAppointments(
            @PathVariable Long veterinarianId) {
        log.info("GET /appointments/vet/{}/today - Fetching today's appointments", veterinarianId);
        List<AppointmentResponseDTO> appointments = appointmentService.getTodayAppointments(veterinarianId);
        return ResponseEntity.ok(appointments);
    }

    /**
     * Confirm an appointment.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponseDTO> confirmAppointment(@PathVariable Long id) {
        log.info("POST /appointments/{}/confirm - Confirming appointment", id);
        AppointmentResponseDTO response = appointmentService.confirmAppointment(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Complete an appointment with medical data.
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponseDTO> completeAppointment(
            @PathVariable Long id,
            @Valid @RequestBody CompleteAppointmentDTO completeDTO) {
        log.info("POST /appointments/{}/complete - Completing appointment", id);
        AppointmentResponseDTO response = appointmentService.completeAppointment(id, completeDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Cancel an appointment.
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponseDTO> cancelAppointment(
            @PathVariable Long id,
            @Valid @RequestBody CancelAppointmentDTO cancelDTO) {
        log.info("POST /appointments/{}/cancel - Cancelling appointment", id);
        AppointmentResponseDTO response = appointmentService.cancelAppointment(id, cancelDTO);
        return ResponseEntity.ok(response);
    }

    /**
     * Check availability of a time slot.
     */
    @GetMapping("/availability")
    public ResponseEntity<Map<String, Boolean>> checkAvailability(
            @RequestParam Long veterinarianId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime time) {
        log.info("GET /appointments/availability - Checking availability for vet: {}, date: {}, time: {}",
                veterinarianId, date, time);
        boolean available = appointmentService.checkAvailability(veterinarianId, date, time);
        return ResponseEntity.ok(Map.of("available", available));
    }
}
