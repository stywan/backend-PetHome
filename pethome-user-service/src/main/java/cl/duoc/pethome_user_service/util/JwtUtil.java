package cl.duoc.pethome_user_service.util;

import cl.duoc.pethome_user_service.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling JWT (JSON Web Token) operations.
 * <p>
 * This component provides methods for generating, validating, and extracting information
 * from JWT tokens used for authentication and authorization in the PetHome system.
 * </p>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String ROLE_CLAIM = "role";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Generates a JWT token for the given email and user role.
     * <p>
     * The token includes the email as the subject, the role as a custom claim,
     * and expiration time based on the configured expiration value.
     * </p>
     *
     * @param email the user's email address (used as the token subject)
     * @param role  the user's role in the system
     * @return a signed JWT token string
     * @throws IllegalArgumentException if email or role is null
     */
    public String generateToken(String email, UserRole role) {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Attempted to generate token with null or empty email");
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (role == null) {
            logger.error("Attempted to generate token with null role");
            throw new IllegalArgumentException("Role cannot be null");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put(ROLE_CLAIM, role.name());

        logger.debug("Generating JWT token for email: {} with role: {}", email, role);

        try {
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setSubject(email)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();

            logger.info("JWT token successfully generated for email: {}", email);
            return token;
        } catch (Exception e) {
            logger.error("Error generating JWT token for email: {}", email, e);
            throw new RuntimeException("Failed to generate JWT token", e);
        }
    }

    /**
     * Extracts the email (subject) from the JWT token.
     *
     * @param token the JWT token string
     * @return the email address stored in the token's subject
     * @throws RuntimeException if the token is invalid or expired
     */
    public String extractEmail(String token) {
        logger.debug("Extracting email from token");
        try {
            String email = extractAllClaims(token).getSubject();
            logger.debug("Email extracted successfully: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Error extracting email from token", e);
            throw e;
        }
    }

    /**
     * Extracts the user role from the JWT token.
     *
     * @param token the JWT token string
     * @return the UserRole stored in the token's custom claims
     * @throws RuntimeException if the token is invalid, expired, or role claim is missing
     */
    public UserRole extractRole(String token) {
        logger.debug("Extracting role from token");
        try {
            Claims claims = extractAllClaims(token);
            String roleString = claims.get(ROLE_CLAIM, String.class);

            if (roleString == null) {
                logger.error("Role claim not found in token");
                throw new RuntimeException("Role claim not found in token");
            }

            UserRole role = UserRole.valueOf(roleString);
            logger.debug("Role extracted successfully: {}", role);
            return role;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid role value in token", e);
            throw new RuntimeException("Invalid role value in token", e);
        } catch (Exception e) {
            logger.error("Error extracting role from token", e);
            throw e;
        }
    }

    /**
     * Validates the JWT token against the provided email.
     * <p>
     * A token is considered valid if:
     * <ul>
     *   <li>The email in the token matches the provided email</li>
     *   <li>The token has not expired</li>
     * </ul>
     * </p>
     *
     * @param token the JWT token string to validate
     * @param email the email to validate against
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token, String email) {
        logger.debug("Validating token for email: {}", email);
        try {
            final String tokenEmail = extractEmail(token);
            boolean isValid = tokenEmail.equals(email) && !isTokenExpired(token);

            if (isValid) {
                logger.info("Token validation successful for email: {}", email);
            } else {
                logger.warn("Token validation failed for email: {}", email);
            }

            return isValid;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired for email: {}", email);
            return false;
        } catch (Exception e) {
            logger.error("Error validating token for email: {}", email, e);
            return false;
        }
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token string
     * @return true if the token has expired, false otherwise
     * @throws RuntimeException if the token is malformed or invalid
     */
    public boolean isTokenExpired(String token) {
        logger.debug("Checking if token is expired");
        try {
            Date expiration = extractExpiration(token);
            boolean isExpired = expiration.before(new Date());
            logger.debug("Token expired status: {}", isExpired);
            return isExpired;
        } catch (Exception e) {
            logger.error("Error checking token expiration", e);
            throw e;
        }
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token string
     * @return the Date when the token expires
     * @throws RuntimeException if the token is invalid or expired
     */
    public Date extractExpiration(String token) {
        logger.debug("Extracting expiration date from token");
        try {
            Date expiration = extractAllClaims(token).getExpiration();
            logger.debug("Expiration date extracted successfully: {}", expiration);
            return expiration;
        } catch (Exception e) {
            logger.error("Error extracting expiration date from token", e);
            throw e;
        }
    }

    /**
     * Extracts all claims from the JWT token.
     * <p>
     * This method parses the token and retrieves all the claims (payload data)
     * stored within it, including both standard claims (subject, expiration, etc.)
     * and custom claims (role, etc.).
     * </p>
     *
     * @param token the JWT token string
     * @return the Claims object containing all token claims
     * @throws ExpiredJwtException      if the token has expired
     * @throws UnsupportedJwtException  if the token format is not supported
     * @throws MalformedJwtException    if the token is malformed
     * @throws SignatureException       if the signature validation fails
     * @throws IllegalArgumentException if the token is null or empty
     */
    public Claims extractAllClaims(String token) {
        logger.debug("Extracting all claims from token");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            logger.debug("All claims extracted successfully");
            return claims;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired", e);
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token", e);
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token", e);
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature", e);
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty", e);
            throw e;
        }
    }

    /**
     * Generates the signing key from the configured secret.
     * <p>
     * This method creates a SecretKey object from the secret string
     * using HMAC-SHA algorithm for signing and verifying JWT tokens.
     * </p>
     *
     * @return the SecretKey for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
