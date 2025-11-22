package cl.duoc.pethome_user_service.repository;

import cl.duoc.pethome_user_service.entity.User;
import cl.duoc.pethome_user_service.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides CRUD operations and custom query methods for User data access.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users with a specific role.
     *
     * @param role the user role to filter by
     * @return a list of users with the specified role
     */
    List<User> findByRole(UserRole role);

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if a user exists with the email, false otherwise
     */
    boolean existsByEmail(String email);
}
