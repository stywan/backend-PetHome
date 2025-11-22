package cl.duoc.pethome_user_service.security;

import cl.duoc.pethome_user_service.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter for processing authentication tokens.
 * <p>
 * This filter intercepts every HTTP request to extract and validate JWT tokens
 * from the Authorization header. If a valid token is found, it sets up the
 * Spring Security authentication context for the request.
 * </p>
 * <p>
 * The filter extends {@link OncePerRequestFilter} to ensure it executes only once
 * per request, even in the case of error dispatches or async requests.
 * </p>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Filters incoming requests to validate JWT tokens and set up authentication.
     * <p>
     * This method performs the following steps:
     * <ol>
     *   <li>Extracts the JWT token from the Authorization header</li>
     *   <li>Validates the token format and signature</li>
     *   <li>Extracts the user email from the token</li>
     *   <li>Loads user details from the database</li>
     *   <li>Creates and sets the authentication in SecurityContextHolder</li>
     * </ol>
     * </p>
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to continue request processing
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs during request processing
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);

            // If token exists and is valid, set up authentication
            if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateToken(jwt, request);
            }

        } catch (ExpiredJwtException e) {
            logger.error("JWT token has expired: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token has expired\", \"message\": \"" + e.getMessage() + "\"}");
            return;

        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Unsupported token\", \"message\": \"" + e.getMessage() + "\"}");
            return;

        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Malformed token\", \"message\": \"" + e.getMessage() + "\"}");
            return;

        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token signature\", \"message\": \"" + e.getMessage() + "\"}");
            return;

        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid token\", \"message\": \"" + e.getMessage() + "\"}");
            return;

        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage(), e);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * <p>
     * This method looks for the "Authorization" header in the request and
     * extracts the token if it starts with "Bearer " prefix.
     * </p>
     *
     * @param request the HTTP request
     * @return the JWT token string without the "Bearer " prefix, or null if not found
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());
            logger.debug("JWT token extracted from Authorization header");
            return token;
        }

        logger.debug("No JWT token found in request headers");
        return null;
    }

    /**
     * Authenticates the JWT token and sets up the security context.
     * <p>
     * This method validates the token, loads the user details, and creates
     * an authentication object that is set in the SecurityContextHolder.
     * </p>
     *
     * @param jwt     the JWT token string
     * @param request the HTTP request
     */
    private void authenticateToken(String jwt, HttpServletRequest request) {
        try {
            // Extract email from token
            String email = jwtUtil.extractEmail(jwt);
            logger.debug("Extracted email from token: {}", email);

            // Load user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Validate token
            if (jwtUtil.validateToken(jwt, email)) {
                logger.info("Token validated successfully for user: {}", email);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Set request details
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Authentication set in SecurityContextHolder for user: {}", email);

            } else {
                logger.warn("Token validation failed for user: {}", email);
            }

        } catch (Exception e) {
            logger.error("Error during token authentication: {}", e.getMessage(), e);
            throw e;
        }
    }
}
