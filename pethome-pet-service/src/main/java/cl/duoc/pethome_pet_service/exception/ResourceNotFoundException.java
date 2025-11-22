package cl.duoc.pethome_pet_service.exception;

/**
 * Exception thrown when a requested resource is not found.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
