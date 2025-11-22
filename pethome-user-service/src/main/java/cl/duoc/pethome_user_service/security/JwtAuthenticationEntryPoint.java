package cl.duoc.pethome_user_service.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Authentication Entry Point for handling authentication errors.
 * <p>
 * This component is invoked when a user tries to access a secured REST resource
 * without proper authentication. It sends a 401 Unauthorized response with a
 * JSON error message to the client.
 * </p>
 * <p>
 * This is particularly useful for REST APIs where you want to return JSON
 * error responses instead of redirecting to a login page.
 * </p>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";

    /**
     * Commences an authentication scheme.
     * <p>
     * This method is called whenever an exception is thrown due to an unauthenticated
     * user trying to access a resource that requires authentication. It constructs
     * a detailed JSON error response and sends it back to the client with a 401
     * Unauthorized status code.
     * </p>
     * <p>
     * The JSON response includes:
     * <ul>
     *   <li>timestamp - when the error occurred</li>
     *   <li>status - HTTP status code (401)</li>
     *   <li>error - error type (Unauthorized)</li>
     *   <li>message - detailed error message</li>
     *   <li>path - the request URI that was accessed</li>
     * </ul>
     * </p>
     *
     * @param request       the HTTP request that resulted in an AuthenticationException
     * @param response      the HTTP response to be sent to the client
     * @param authException the exception that was thrown
     * @throws IOException      if an input or output exception occurs
     * @throws ServletException if a servlet-specific error occurs
     */
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        logger.error("Unauthorized error: {} - Path: {}", authException.getMessage(), request.getRequestURI());

        // Set response status and content type
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(CONTENT_TYPE_JSON);

        // Build error response body
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorDetails.put("error", "Unauthorized");
        errorDetails.put("message", getErrorMessage(authException));
        errorDetails.put("path", request.getRequestURI());

        // Log detailed error information
        logger.debug("Authentication error details: {}", errorDetails);

        // Write JSON response
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(errorDetails);
            response.getWriter().write(jsonResponse);
            response.getWriter().flush();

            logger.debug("Unauthorized response sent successfully");
        } catch (IOException e) {
            logger.error("Error writing unauthorized response: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Extracts a user-friendly error message from the authentication exception.
     * <p>
     * This method provides a more meaningful error message to the client
     * based on the type of authentication exception that occurred.
     * </p>
     *
     * @param authException the authentication exception
     * @return a user-friendly error message
     */
    private String getErrorMessage(AuthenticationException authException) {
        if (authException == null) {
            return "Authentication is required to access this resource";
        }

        String exceptionMessage = authException.getMessage();

        // Provide specific messages for common authentication failures
        if (exceptionMessage == null || exceptionMessage.isEmpty()) {
            return "Authentication failed. Please provide valid credentials.";
        }

        if (exceptionMessage.contains("Bad credentials")) {
            return "Invalid username or password";
        }

        if (exceptionMessage.contains("User is disabled")) {
            return "User account is disabled";
        }

        if (exceptionMessage.contains("User account has expired")) {
            return "User account has expired";
        }

        if (exceptionMessage.contains("User account is locked")) {
            return "User account is locked";
        }

        // Return the original exception message for other cases
        return exceptionMessage;
    }
}
