package cl.duoc.pethome_user_service.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra un recurso solicitado.
 *
 * Esta excepción extiende RuntimeException, lo que la convierte en una excepción no verificada
 * que puede ser capturada por el manejador global de excepciones.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-06
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor que crea una nueva excepción con el mensaje especificado.
     *
     * @param message el mensaje de detalle que describe el recurso no encontrado
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor que crea una nueva excepción con el mensaje y la causa especificados.
     *
     * @param message el mensaje de detalle que describe el recurso no encontrado
     * @param cause la causa de la excepción (puede ser null)
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
