package cl.duoc.pethome_veterinarian_service.exception;

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
 * Global exception handler for the Veterinarian Service.
 *
 * This class captures and processes all exceptions thrown in REST controllers,
 * providing consistent and structured error responses to the client.
 *
 * Uses @RestControllerAdvice to apply exception handling to all controllers
 * in the application centrally.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles ResourceNotFoundException exceptions.
     *
     * Thrown when a requested resource is not found (veterinarian, schedule, etc.).
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {

        logger.error("Resource not found: {}", ex.getMessage());

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
     * Handles IllegalArgumentException exceptions.
     *
     * Thrown when an argument is invalid or does not meet expected conditions.
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request) {

        logger.error("Illegal argument: {}", ex.getMessage());

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
     * Handles method argument validation exceptions.
     *
     * Thrown when Bean Validation fails (@Valid, @NotNull, etc.).
     * Extracts all validation errors and includes them in the response.
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        logger.error("Validation error: {}", ex.getMessage());

        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            validationErrors.put(error.getField(), error.getDefaultMessage())
        );

        String errorMessage = "Validation error: " + validationErrors.toString();

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
     * Handles exceptions when HTTP message body is not readable.
     *
     * Thrown when the JSON sent is malformed or cannot be deserialized.
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 400 BAD REQUEST
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        logger.error("HTTP message not readable: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "The message format is not valid. Check the JSON syntax.",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles RuntimeException exceptions.
     *
     * Thrown for runtime errors not specifically handled.
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex,
            WebRequest request) {

        logger.error("Runtime error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handles all other exceptions not specifically caught.
     *
     * This is the catch-all handler that captures any unhandled exception
     * by the other handlers.
     *
     * @param ex the caught exception
     * @param request the current web request
     * @return ResponseEntity with ErrorResponse and HTTP status 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {

        logger.error("Unhandled error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "An unexpected error occurred. Please contact the administrator.",
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Inner class representing the error response structure.
     *
     * Provides a consistent format for all API error responses.
     */
    public static class ErrorResponse {

        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        /**
         * Constructor to create a complete error response.
         *
         * @param timestamp timestamp when the error occurred
         * @param status HTTP status code
         * @param error short description of the error type
         * @param message detailed error message
         * @param path request path that caused the error
         */
        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        // Getters and Setters

        /**
         * Gets the error timestamp.
         *
         * @return LocalDateTime with the error date and time
         */
        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the error timestamp.
         *
         * @param timestamp LocalDateTime with the error date and time
         */
        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }

        /**
         * Gets the HTTP status code.
         *
         * @return HTTP status code
         */
        public int getStatus() {
            return status;
        }

        /**
         * Sets the HTTP status code.
         *
         * @param status HTTP status code
         */
        public void setStatus(int status) {
            this.status = status;
        }

        /**
         * Gets the short error description.
         *
         * @return error type description
         */
        public String getError() {
            return error;
        }

        /**
         * Sets the short error description.
         *
         * @param error error type description
         */
        public void setError(String error) {
            this.error = error;
        }

        /**
         * Gets the detailed error message.
         *
         * @return descriptive error message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the detailed error message.
         *
         * @param message descriptive error message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Gets the request path that caused the error.
         *
         * @return request URI path
         */
        public String getPath() {
            return path;
        }

        /**
         * Sets the request path that caused the error.
         *
         * @param path request URI path
         */
        public void setPath(String path) {
            this.path = path;
        }
    }
}
