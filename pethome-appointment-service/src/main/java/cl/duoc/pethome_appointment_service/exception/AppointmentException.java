package cl.duoc.pethome_appointment_service.exception;

/**
 * Exception for appointment-specific business logic errors.
 */
public class AppointmentException extends RuntimeException {

    public AppointmentException(String message) {
        super(message);
    }
}
