package cl.duoc.pethome_veterinarian_service.service.impl;

import cl.duoc.pethome_veterinarian_service.dto.VeterinarianRequestDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianResponseDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianScheduleDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianSearchDTO;
import cl.duoc.pethome_veterinarian_service.dto.VeterinarianUpdateDTO;
import cl.duoc.pethome_veterinarian_service.entity.Veterinarian;
import cl.duoc.pethome_veterinarian_service.entity.VeterinarianSchedule;
import cl.duoc.pethome_veterinarian_service.exception.ResourceNotFoundException;
import cl.duoc.pethome_veterinarian_service.repository.VeterinarianRepository;
import cl.duoc.pethome_veterinarian_service.repository.VeterinarianScheduleRepository;
import cl.duoc.pethome_veterinarian_service.service.VeterinarianService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of VeterinarianService interface.
 *
 * Provides business logic for managing veterinarians in the PetHome system.
 * Handles CRUD operations, search functionality, and data transformation between
 * entities and DTOs.
 *
 * @author PetHome Team
 * @version 1.0
 * @since 2024-11-07
 */
@Service
@Transactional
public class VeterinarianServiceImpl implements VeterinarianService {

    private static final Logger logger = LoggerFactory.getLogger(VeterinarianServiceImpl.class);

    private final VeterinarianRepository veterinarianRepository;
    private final VeterinarianScheduleRepository veterinarianScheduleRepository;

    /**
     * Constructor for dependency injection.
     *
     * @param veterinarianRepository the veterinarian repository
     * @param veterinarianScheduleRepository the veterinarian schedule repository
     */
    public VeterinarianServiceImpl(VeterinarianRepository veterinarianRepository,
                                   VeterinarianScheduleRepository veterinarianScheduleRepository) {
        this.veterinarianRepository = veterinarianRepository;
        this.veterinarianScheduleRepository = veterinarianScheduleRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VeterinarianResponseDTO createVeterinarian(VeterinarianRequestDTO request) {
        logger.info("Creating veterinarian for user ID: {}", request.getUserId());

        // Validate that user ID doesn't already have a veterinarian profile
        if (veterinarianRepository.existsByUserId(request.getUserId())) {
            logger.error("User ID {} already has a veterinarian profile", request.getUserId());
            throw new IllegalArgumentException("User ID already has a veterinarian profile");
        }

        // Validate that license number is unique
        if (veterinarianRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            logger.error("License number {} already exists", request.getLicenseNumber());
            throw new IllegalArgumentException("License number already exists");
        }

        // Map DTO to entity
        Veterinarian veterinarian = Veterinarian.builder()
                .userId(request.getUserId())
                .specialty(request.getSpecialty())
                .licenseNumber(request.getLicenseNumber())
                .clinicName(request.getClinicName())
                .clinicAddress(request.getClinicAddress())
                .workingHours(request.getWorkingHours())
                .bio(request.getBio())
                .experienceYears(request.getExperienceYears())
                .photo(request.getPhoto())
                .isAvailable(true)
                .rating(0.0)
                .build();

        Veterinarian savedVeterinarian = veterinarianRepository.save(veterinarian);
        logger.info("Veterinarian created successfully with ID: {}", savedVeterinarian.getId());

        return mapToResponseDTO(savedVeterinarian);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public VeterinarianResponseDTO getVeterinarianById(Long id) {
        logger.info("Retrieving veterinarian with ID: {}", id);

        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Veterinarian not found with ID: {}", id);
                    return new ResourceNotFoundException("Veterinarian not found with ID: " + id);
                });

        logger.info("Veterinarian found with ID: {}", id);
        return mapToResponseDTO(veterinarian);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public VeterinarianResponseDTO getVeterinarianByUserId(Long userId) {
        logger.info("Retrieving veterinarian for user ID: {}", userId);

        Veterinarian veterinarian = veterinarianRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    logger.error("Veterinarian not found for user ID: {}", userId);
                    return new ResourceNotFoundException("Veterinarian not found for user ID: " + userId);
                });

        logger.info("Veterinarian found for user ID: {}", userId);
        return mapToResponseDTO(veterinarian);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VeterinarianResponseDTO> getAllVeterinarians() {
        logger.info("Retrieving all veterinarians");

        List<Veterinarian> veterinarians = veterinarianRepository.findAll();
        logger.info("Found {} veterinarians", veterinarians.size());

        return veterinarians.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VeterinarianResponseDTO> getAvailableVeterinarians() {
        logger.info("Retrieving all available veterinarians");

        List<Veterinarian> veterinarians = veterinarianRepository.findByIsAvailableTrue();
        logger.info("Found {} available veterinarians", veterinarians.size());

        return veterinarians.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<VeterinarianResponseDTO> searchVeterinarians(VeterinarianSearchDTO search) {
        logger.info("Searching veterinarians with criteria: {}", search);

        List<Veterinarian> veterinarians = veterinarianRepository.findAll();

        // Filter by specialty if provided
        if (search.getSpecialty() != null && !search.getSpecialty().isEmpty()) {
            veterinarians = veterinarians.stream()
                    .filter(v -> v.getSpecialty().toLowerCase()
                            .contains(search.getSpecialty().toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Filter by availability if provided
        if (search.getIsAvailable() != null) {
            veterinarians = veterinarians.stream()
                    .filter(v -> v.getIsAvailable().equals(search.getIsAvailable()))
                    .collect(Collectors.toList());
        }

        // Filter by minimum rating if provided
        if (search.getMinRating() != null) {
            veterinarians = veterinarians.stream()
                    .filter(v -> v.getRating() >= search.getMinRating())
                    .collect(Collectors.toList());
        }

        logger.info("Search returned {} veterinarians", veterinarians.size());

        return veterinarians.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VeterinarianResponseDTO updateVeterinarian(Long id, VeterinarianUpdateDTO update) {
        logger.info("Updating veterinarian with ID: {}", id);

        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Veterinarian not found with ID: {}", id);
                    return new ResourceNotFoundException("Veterinarian not found with ID: " + id);
                });

        // Update only non-null fields
        if (update.getSpecialty() != null) {
            veterinarian.setSpecialty(update.getSpecialty());
        }
        if (update.getClinicName() != null) {
            veterinarian.setClinicName(update.getClinicName());
        }
        if (update.getClinicAddress() != null) {
            veterinarian.setClinicAddress(update.getClinicAddress());
        }
        if (update.getWorkingHours() != null) {
            veterinarian.setWorkingHours(update.getWorkingHours());
        }
        if (update.getBio() != null) {
            veterinarian.setBio(update.getBio());
        }
        if (update.getIsAvailable() != null) {
            veterinarian.setIsAvailable(update.getIsAvailable());
        }
        if (update.getExperienceYears() != null) {
            veterinarian.setExperienceYears(update.getExperienceYears());
        }
        if (update.getPhoto() != null) {
            veterinarian.setPhoto(update.getPhoto());
        }

        Veterinarian updatedVeterinarian = veterinarianRepository.save(veterinarian);
        logger.info("Veterinarian updated successfully with ID: {}", id);

        return mapToResponseDTO(updatedVeterinarian);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteVeterinarian(Long id) {
        logger.info("Deleting veterinarian with ID: {}", id);

        Veterinarian veterinarian = veterinarianRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Veterinarian not found with ID: {}", id);
                    return new ResourceNotFoundException("Veterinarian not found with ID: " + id);
                });

        veterinarianRepository.delete(veterinarian);
        logger.info("Veterinarian deleted successfully with ID: {}", id);
    }

    /**
     * Maps a Veterinarian entity to a VeterinarianResponseDTO.
     *
     * @param veterinarian the entity to map
     * @return the mapped DTO
     */
    private VeterinarianResponseDTO mapToResponseDTO(Veterinarian veterinarian) {
        List<VeterinarianScheduleDTO> scheduleDTOs = veterinarian.getSchedules().stream()
                .map(this::mapScheduleToDTO)
                .collect(Collectors.toList());

        return VeterinarianResponseDTO.builder()
                .id(veterinarian.getId())
                .userId(veterinarian.getUserId())
                .specialty(veterinarian.getSpecialty())
                .licenseNumber(veterinarian.getLicenseNumber())
                .clinicName(veterinarian.getClinicName())
                .clinicAddress(veterinarian.getClinicAddress())
                .workingHours(veterinarian.getWorkingHours())
                .bio(veterinarian.getBio())
                .isAvailable(veterinarian.getIsAvailable())
                .rating(veterinarian.getRating())
                .experienceYears(veterinarian.getExperienceYears())
                .photo(veterinarian.getPhoto())
                .createdAt(veterinarian.getCreatedAt())
                .updatedAt(veterinarian.getUpdatedAt())
                .schedules(scheduleDTOs)
                .build();
    }

    /**
     * Maps a VeterinarianSchedule entity to a VeterinarianScheduleDTO.
     *
     * @param schedule the entity to map
     * @return the mapped DTO
     */
    private VeterinarianScheduleDTO mapScheduleToDTO(VeterinarianSchedule schedule) {
        return VeterinarianScheduleDTO.builder()
                .id(schedule.getId())
                .dayOfWeek(schedule.getDayOfWeek())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .isActive(schedule.getIsActive())
                .build();
    }
}
