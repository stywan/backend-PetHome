package cl.duoc.pethome_veterinarian_service.exception;

/**
 * Custom exception thrown when a requested resource is not found.
 *
 * This exception extends RuntimeException, making it an unchecked exception
 * that can be caught by the global exception handler.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor that creates a new exception with the specified message.
     *
     * @param message the detail message describing the resource not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor that creates a new exception with the specified message and cause.
     *
     * @param message the detail message describing the resource not found
     * @param cause the cause of the exception (can be null)
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
