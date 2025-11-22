package cl.duoc.pethome_user_service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para el User Service.
 *
 * Esta clase captura y procesa todas las excepciones lanzadas en los controladores REST,
 * proporcionando respuestas de error consistentes y estructuradas para el cliente.
 *
 * Utiliza @RestControllerAdvice para aplicar el manejo de excepciones a todos los controladores
 * de la aplicación de forma centralizada.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-06
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja excepciones de tipo ResourceNotFoundException.
     *
     * Se lanza cuando no se encuentra un recurso solicitado (usuario, rol, etc.).
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        logger.error("Recurso no encontrado: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones de tipo IllegalArgumentException.
     *
     * Se lanza cuando un argumento no es válido o no cumple con las condiciones esperadas.
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.error("Argumento ilegal: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de validación de argumentos de métodos.
     *
     * Se lanza cuando fallan las validaciones de Bean Validation (@Valid, @NotNull, etc.).
     * Extrae todos los errores de validación y los incluye en la respuesta.
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.error("Error de validación: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        String errorMessage = "Error de validación: " + validationErrors.toString();

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                errorMessage,
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones cuando el cuerpo del mensaje HTTP no es legible.
     *
     * Se lanza cuando el JSON enviado está malformado o no puede ser deserializado.
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        logger.error("Mensaje HTTP no legible: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "El formato del mensaje no es válido. Verifica la sintaxis JSON.",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja excepciones de tipo RuntimeException.
     *
     * Se lanza para errores en tiempo de ejecución no manejados específicamente.
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        logger.error("Error de ejecución: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Error interno del servidor: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Maneja todas las demás excepciones no capturadas específicamente.
     *
     * Este es el manejador catch-all que captura cualquier excepción no manejada
     * por los otros handlers.
     *
     * @param ex la excepción capturada
     * @param request la petición web actual
     * @return ResponseEntity con ErrorResponse y estado HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Error no manejado: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Clase interna que representa la estructura de respuesta de error.
     *
     * Proporciona un formato consistente para todas las respuestas de error de la API.
     */
    public static class ErrorResponse {

        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        /**
         * Constructor para crear una respuesta de error completa.
         *
         * @param timestamp marca de tiempo cuando ocurrió el error
         * @param status código de estado HTTP
         * @param error descripción corta del tipo de error
         * @param message mensaje detallado del error
         * @param path ruta de la petición que causó el error
         */
        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        // Getters y Setters

        /**
         * Obtiene la marca de tiempo del error.
         *
         * @return LocalDateTime con la fecha y hora del error
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        /**
         * Establece la marca de tiempo del error.
         *
         * @param timestamp LocalDateTime con la fecha y hora del error
         */
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Obtiene el código de estado HTTP.
         *
         * @return código de estado HTTP
         */
        public int getStatus() {
            return status;
        }

        /**
         * Establece el código de estado HTTP.
         *
         * @param status código de estado HTTP
         */
        public void setStatus(int status) {
            this.status = status;
        }

        /**
         * Obtiene la descripción corta del error.
         *
         * @return descripción del tipo de error
         */
        public String getError() {
            return error;
        }

        /**
         * Establece la descripción corta del error.
         *
         * @param error descripción del tipo de error
         */
        public void setError(String error) {
            this.error = error;
        }

        /**
         * Obtiene el mensaje detallado del error.
         *
         * @return mensaje descriptivo del error
         */
        public String getMessage() {
            return message;
        }

        /**
         * Establece el mensaje detallado del error.
         *
         * @param message mensaje descriptivo del error
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Obtiene la ruta de la petición que causó el error.
         *
         * @return ruta URI de la petición
         */
        public String getPath() {
            return path;
        }

        /**
         * Establece la ruta de la petición que causó el error.
         *
         * @param path ruta URI de la petición
         */
        public void setPath(String path) {
            this.path = path;
        }
    }
}
