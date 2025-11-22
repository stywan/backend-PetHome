package cl.duoc.pethome_user_service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the PetHome User Service.
 * <p>
 * This configuration class sets up Spring Security for the application with JWT-based
 * authentication. It configures:
 * <ul>
 *   <li>JWT authentication filter</li>
 *   <li>Session management (stateless)</li>
 *   <li>Authorization rules for different endpoints</li>
 *   <li>Password encoding</li>
 *   <li>Authentication manager</li>
 * </ul>
 * </p>
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Configures the security filter chain.
     * <p>
     * This method sets up the main security configuration including:
     * <ul>
     *   <li>CSRF protection (disabled for REST APIs)</li>
     *   <li>Session management (stateless for JWT)</li>
     *   <li>Authorization rules for endpoints</li>
     *   <li>JWT authentication filter</li>
     *   <li>Exception handling</li>
     * </ul>
     * </p>
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain");

        http
                // Disable CSRF as we're using JWT tokens (stateless authentication)
                .csrf(AbstractHttpConfigurer::disable)

                // Configure exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                // Configure session management as STATELESS (no session will be created or used)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - no authentication required
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Allow H2 console to be displayed in frames (for development only)
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        // Add JWT authentication filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("Security filter chain configured successfully");

        return http.build();
    }

    /**
     * Creates a BCrypt password encoder bean.
     * <p>
     * BCrypt is a strong hashing algorithm that includes a salt to protect against
     * rainbow table attacks. It is designed to be slow to defend against brute-force
     * attacks.
     * </p>
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("Creating BCryptPasswordEncoder bean");
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates an authentication manager bean.
     * <p>
     * The AuthenticationManager is responsible for authenticating users.
     * It uses the configured AuthenticationProvider to validate credentials.
     * </p>
     *
     * @param authenticationConfiguration the authentication configuration
     * @return the configured AuthenticationManager
     * @throws Exception if an error occurs while creating the authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        logger.debug("Creating AuthenticationManager bean");
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates a DaoAuthenticationProvider bean.
     * <p>
     * This provider uses a UserDetailsService to retrieve user information
     * and a PasswordEncoder to validate passwords. It is the standard way
     * to authenticate users against a database.
     * </p>
     *
     * @return a configured DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.debug("Creating DaoAuthenticationProvider bean");

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        logger.info("DaoAuthenticationProvider configured with CustomUserDetailsService and BCryptPasswordEncoder");

        return authProvider;
    }
}
