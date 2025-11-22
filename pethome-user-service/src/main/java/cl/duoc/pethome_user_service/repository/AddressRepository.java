package cl.duoc.pethome_user_service.repository;

import cl.duoc.pethome_user_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Address entities.
 * Provides CRUD operations and custom query methods for Address data access.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Finds all addresses associated with a specific user.
     *
     * @param userId the ID of the user whose addresses to retrieve
     * @return a list of addresses belonging to the specified user
     */
    List<Address> findByUserId(Long userId);

    /**
     * Finds the default address for a specific user.
     *
     * @param userId the ID of the user whose default address to retrieve
     * @return an Optional containing the default address if found, or empty if not found
     */
    Optional<Address> findByUserIdAndIsDefaultTrue(Long userId);
}
