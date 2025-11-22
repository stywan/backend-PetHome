package cl.duoc.pethome_user_service.security;

import cl.duoc.pethome_user_service.entity.User;
import cl.duoc.pethome_user_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * <p>
 * This service is responsible for loading user-specific data during authentication.
 * It retrieves user information from the database and converts it into a format
 * that Spring Security can work with (UserDetails).
 * </p>
 * <p>
 * The service uses the user's email as the username for authentication purposes.
 * User roles are converted to Spring Security authorities with the "ROLE_" prefix.
 * </p>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user details by email address (username).
     * <p>
     * This method is called by Spring Security during authentication to retrieve
     * user information. It searches for a user by email and converts the entity
     * into a UserDetails object that includes:
     * <ul>
     *   <li>Email as the username</li>
     *   <li>Encrypted password</li>
     *   <li>Authorities based on the user's role</li>
     * </ul>
     * </p>
     *
     * @param email the email address (username) of the user to load
     * @return a UserDetails object containing the user's authentication information
     * @throws UsernameNotFoundException if no user is found with the given email
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        // Validate input
        if (email == null || email.trim().isEmpty()) {
            logger.error("Attempted to load user with null or empty email");
            throw new UsernameNotFoundException("Email cannot be null or empty");
        }

        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        logger.info("User found successfully with email: {}", email);

        // Convert user role to Spring Security authorities
        Collection<GrantedAuthority> authorities = getAuthorities(user);

        // Create and return UserDetails object
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();

        logger.debug("UserDetails created successfully for user: {}", email);

        return userDetails;
    }

    /**
     * Converts user role to Spring Security authorities.
     * <p>
     * This method creates a collection of GrantedAuthority objects based on the
     * user's role. The role is prefixed with "ROLE_" as per Spring Security convention.
     * For example, a user with role "CLIENT" will have the authority "ROLE_CLIENT".
     * </p>
     *
     * @param user the user entity
     * @return a collection of GrantedAuthority objects
     */
    private Collection<GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user.getRole() != null) {
            String roleName = ROLE_PREFIX + user.getRole().name();
            authorities.add(new SimpleGrantedAuthority(roleName));
            logger.debug("Added authority: {} for user: {}", roleName, user.getEmail());
        } else {
            logger.warn("User {} has no role assigned", user.getEmail());
        }

        return authorities;
    }

    /**
     * Loads user by ID (optional utility method).
     * <p>
     * This is a convenience method that can be used when you need to load
     * a user by their ID rather than email. This is useful for operations
     * that have the user ID but not the email.
     * </p>
     *
     * @param userId the user ID
     * @return the User entity
     * @throws UsernameNotFoundException if no user is found with the given ID
     */
    public User loadUserById(Long userId) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by ID: {}", userId);

        if (userId == null) {
            logger.error("Attempted to load user with null ID");
            throw new UsernameNotFoundException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new UsernameNotFoundException("User not found with ID: " + userId);
                });

        logger.info("User found successfully with ID: {}", userId);
        return user;
    }
}
