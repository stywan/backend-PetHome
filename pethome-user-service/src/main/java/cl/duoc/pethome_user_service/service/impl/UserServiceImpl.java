package cl.duoc.pethome_user_service.service.impl;

import cl.duoc.pethome_user_service.dto.AddressDTO;
import cl.duoc.pethome_user_service.dto.UserRequestDTO;
import cl.duoc.pethome_user_service.dto.UserResponseDTO;
import cl.duoc.pethome_user_service.dto.UserUpdateDTO;
import cl.duoc.pethome_user_service.entity.Address;
import cl.duoc.pethome_user_service.entity.User;
import cl.duoc.pethome_user_service.enums.UserRole;
import cl.duoc.pethome_user_service.exception.ResourceNotFoundException;
import cl.duoc.pethome_user_service.repository.AddressRepository;
import cl.duoc.pethome_user_service.repository.UserRepository;
import cl.duoc.pethome_user_service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the UserService interface.
 * Provides business logic for user management operations including CRUD operations,
 * password encryption, and data mapping between entities and DTOs.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor with dependency injection.
     *
     * @param userRepository    the user repository
     * @param addressRepository the address repository
     * @param passwordEncoder   the password encoder for hashing passwords
     */
    public UserServiceImpl(UserRepository userRepository,
                           AddressRepository addressRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user in the system.
     * Validates that the email is unique and encrypts the password before saving.
     *
     * @param request the user creation request containing user details
     * @return the created user as a response DTO
     * @throws ResourceNotFoundException if the email already exists
     */
    @Override
    public UserResponseDTO createUser(UserRequestDTO request) {
        logger.info("Creating new user with email: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.error("Email already exists: {}", request.getEmail());
            throw new ResourceNotFoundException("Email already exists: " + request.getEmail());
        }

        // Create user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole() != null ? request.getRole() : UserRole.CLIENT)
                .photo(request.getPhoto())
                .build();

        // Save user
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return mapToResponseDTO(savedUser);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user's unique identifier
     * @return the user as a response DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        logger.debug("User found: {}", user.getEmail());
        return mapToResponseDTO(user);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email the user's email address
     * @return the user as a response DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO getUserByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });

        logger.debug("User found with ID: {}", user.getId());
        return mapToResponseDTO(user);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a list of all users as response DTOs
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.debug("Fetching all users");

        List<User> users = userRepository.findAll();
        logger.info("Found {} users", users.size());

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all users with a specific role.
     *
     * @param role the user role to filter by
     * @return a list of users with the specified role
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getUsersByRole(UserRole role) {
        logger.debug("Fetching users with role: {}", role);

        List<User> users = userRepository.findByRole(role);
        logger.info("Found {} users with role: {}", users.size(), role);

        return users.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing user's information.
     * Only updates fields that are provided in the update DTO.
     *
     * @param id     the user's unique identifier
     * @param update the update DTO containing fields to update
     * @return the updated user as a response DTO
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    public UserResponseDTO updateUser(Long id, UserUpdateDTO update) {
        logger.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with ID: " + id);
                });

        // Update only provided fields
        if (update.getName() != null) {
            user.setName(update.getName());
            logger.debug("Updated name for user ID: {}", id);
        }
        if (update.getPhone() != null) {
            user.setPhone(update.getPhone());
            logger.debug("Updated phone for user ID: {}", id);
        }
        if (update.getPhoto() != null) {
            user.setPhoto(update.getPhoto());
            logger.debug("Updated photo for user ID: {}", id);
        }

        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {}", id);

        return mapToResponseDTO(updatedUser);
    }

    /**
     * Deletes a user from the system.
     *
     * @param id the user's unique identifier
     * @throws ResourceNotFoundException if the user is not found
     */
    @Override
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.error("User not found with ID: {}", id);
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }

    /**
     * Checks if a user exists with the given email address.
     *
     * @param email the email address to check
     * @return true if a user exists with the email, false otherwise
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        logger.debug("Checking if email exists: {}", email);
        boolean exists = userRepository.existsByEmail(email);
        logger.debug("Email {} exists: {}", email, exists);
        return exists;
    }

    /**
     * Maps a User entity to a UserResponseDTO.
     * Includes all user information except the password.
     *
     * @param user the user entity to map
     * @return the mapped user response DTO
     */
    private UserResponseDTO mapToResponseDTO(User user) {
        List<AddressDTO> addressDTOs = user.getAddresses().stream()
                .map(this::mapAddressToDTO)
                .collect(Collectors.toList());

        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .photo(user.getPhoto())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .addresses(addressDTOs)
                .build();
    }

    /**
     * Maps an Address entity to an AddressDTO.
     *
     * @param address the address entity to map
     * @return the mapped address DTO
     */
    private AddressDTO mapAddressToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .neighborhood(address.getNeighborhood())
                .zipCode(address.getZipCode())
                .isDefault(address.getIsDefault())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }
}
