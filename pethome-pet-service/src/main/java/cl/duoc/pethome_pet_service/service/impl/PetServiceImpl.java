package cl.duoc.pethome_pet_service.service.impl;

import cl.duoc.pethome_pet_service.dto.*;
import cl.duoc.pethome_pet_service.entity.MedicalRecord;
import cl.duoc.pethome_pet_service.entity.Pet;
import cl.duoc.pethome_pet_service.exception.ResourceNotFoundException;
import cl.duoc.pethome_pet_service.repository.MedicalRecordRepository;
import cl.duoc.pethome_pet_service.repository.PetRepository;
import cl.duoc.pethome_pet_service.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PetService interface.
 * Handles business logic for pet and medical record operations.
 *
 * @author PetHome Development Team
 * @version 1.0
 * @since 2024
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PetServiceImpl implements PetService {

    private final PetRepository petRepository;
    private final MedicalRecordRepository medicalRecordRepository;

    @Override
    public PetResponseDTO createPet(PetRequestDTO petRequestDTO) {
        log.info("Creating new pet with name: {}", petRequestDTO.getName());

        Pet pet = Pet.builder()
                .name(petRequestDTO.getName())
                .species(petRequestDTO.getSpecies())
                .breed(petRequestDTO.getBreed())
                .birthDate(petRequestDTO.getBirthDate())
                .gender(petRequestDTO.getGender())
                .color(petRequestDTO.getColor())
                .weight(petRequestDTO.getWeight())
                .medicalConditions(petRequestDTO.getMedicalConditions())
                .allergies(petRequestDTO.getAllergies())
                .ownerId(petRequestDTO.getOwnerId())
                .isActive(true)
                .build();

        Pet savedPet = petRepository.save(pet);
        log.info("Pet created successfully with ID: {}", savedPet.getId());

        return mapToResponseDTO(savedPet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDTO> getAllPets() {
        log.info("Fetching all pets");
        List<Pet> pets = petRepository.findAll();
        return pets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PetResponseDTO getPetById(Long id) {
        log.info("Fetching pet with ID: {}", id);
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + id));
        return mapToResponseDTO(pet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDTO> getPetsByOwnerId(Long ownerId) {
        log.info("Fetching pets for owner ID: {}", ownerId);
        List<Pet> pets = petRepository.findByOwnerId(ownerId);
        return pets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDTO> getActivePetsByOwnerId(Long ownerId) {
        log.info("Fetching active pets for owner ID: {}", ownerId);
        List<Pet> pets = petRepository.findByOwnerIdAndIsActive(ownerId, true);
        return pets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDTO> getPetsBySpecies(String species) {
        log.info("Fetching pets by species: {}", species);
        List<Pet> pets = petRepository.findBySpeciesIgnoreCase(species);
        return pets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PetResponseDTO> searchPetsByName(String name) {
        log.info("Searching pets by name: {}", name);
        List<Pet> pets = petRepository.findByNameContainingIgnoreCase(name);
        return pets.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PetResponseDTO updatePet(Long id, PetUpdateDTO petUpdateDTO) {
        log.info("Updating pet with ID: {}", id);

        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + id));

        if (petUpdateDTO.getName() != null) {
            pet.setName(petUpdateDTO.getName());
        }
        if (petUpdateDTO.getBreed() != null) {
            pet.setBreed(petUpdateDTO.getBreed());
        }
        if (petUpdateDTO.getGender() != null) {
            pet.setGender(petUpdateDTO.getGender());
        }
        if (petUpdateDTO.getColor() != null) {
            pet.setColor(petUpdateDTO.getColor());
        }
        if (petUpdateDTO.getWeight() != null) {
            pet.setWeight(petUpdateDTO.getWeight());
        }
        if (petUpdateDTO.getMedicalConditions() != null) {
            pet.setMedicalConditions(petUpdateDTO.getMedicalConditions());
        }
        if (petUpdateDTO.getAllergies() != null) {
            pet.setAllergies(petUpdateDTO.getAllergies());
        }
        if (petUpdateDTO.getIsActive() != null) {
            pet.setIsActive(petUpdateDTO.getIsActive());
        }

        Pet updatedPet = petRepository.save(pet);
        log.info("Pet updated successfully with ID: {}", updatedPet.getId());

        return mapToResponseDTO(updatedPet);
    }

    @Override
    public void deletePet(Long id) {
        log.info("Deleting pet with ID: {}", id);

        if (!petRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + id);
        }

        petRepository.deleteById(id);
        log.info("Pet deleted successfully with ID: {}", id);
    }

    @Override
    public MedicalRecordResponseDTO addMedicalRecord(MedicalRecordRequestDTO medicalRecordRequestDTO) {
        log.info("Adding medical record for pet ID: {}", medicalRecordRequestDTO.getPetId());

        Pet pet = petRepository.findById(medicalRecordRequestDTO.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Pet not found with ID: " + medicalRecordRequestDTO.getPetId()));

        MedicalRecord medicalRecord = MedicalRecord.builder()
                .pet(pet)
                .veterinarianId(medicalRecordRequestDTO.getVeterinarianId())
                .visitDate(medicalRecordRequestDTO.getVisitDate())
                .recordType(medicalRecordRequestDTO.getRecordType())
                .diagnosis(medicalRecordRequestDTO.getDiagnosis())
                .treatment(medicalRecordRequestDTO.getTreatment())
                .medications(medicalRecordRequestDTO.getMedications())
                .notes(medicalRecordRequestDTO.getNotes())
                .weight(medicalRecordRequestDTO.getWeight())
                .temperature(medicalRecordRequestDTO.getTemperature())
                .heartRate(medicalRecordRequestDTO.getHeartRate())
                .cost(medicalRecordRequestDTO.getCost())
                .nextVisitDate(medicalRecordRequestDTO.getNextVisitDate())
                .build();

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created successfully with ID: {}", savedRecord.getId());

        return mapToMedicalRecordResponseDTO(savedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponseDTO> getMedicalRecordsByPetId(Long petId) {
        log.info("Fetching medical records for pet ID: {}", petId);

        if (!petRepository.existsById(petId)) {
            throw new ResourceNotFoundException("Pet not found with ID: " + petId);
        }

        List<MedicalRecord> records = medicalRecordRepository.findByPetId(petId);
        return records.stream()
                .map(this::mapToMedicalRecordResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponseDTO getMedicalRecordById(Long id) {
        log.info("Fetching medical record with ID: {}", id);
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + id));
        return mapToMedicalRecordResponseDTO(record);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponseDTO> getMedicalRecordsByVeterinarianId(Long veterinarianId) {
        log.info("Fetching medical records for veterinarian ID: {}", veterinarianId);
        List<MedicalRecord> records = medicalRecordRepository.findByVeterinarianIdOrderByVisitDateDesc(veterinarianId);
        return records.stream()
                .map(this::mapToMedicalRecordResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMedicalRecord(Long id) {
        log.info("Deleting medical record with ID: {}", id);

        if (!medicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Medical record not found with ID: " + id);
        }

        medicalRecordRepository.deleteById(id);
        log.info("Medical record deleted successfully with ID: {}", id);
    }

    /**
     * Maps Pet entity to PetResponseDTO.
     */
    private PetResponseDTO mapToResponseDTO(Pet pet) {
        return PetResponseDTO.builder()
                .id(pet.getId())
                .name(pet.getName())
                .species(pet.getSpecies())
                .breed(pet.getBreed())
                .birthDate(pet.getBirthDate())
                .gender(pet.getGender())
                .color(pet.getColor())
                .weight(pet.getWeight())
                .medicalConditions(pet.getMedicalConditions())
                .allergies(pet.getAllergies())
                .ownerId(pet.getOwnerId())
                .isActive(pet.getIsActive())
                .age(pet.getAge())
                .formattedAge(pet.getFormattedAge())
                .createdAt(pet.getCreatedAt())
                .updatedAt(pet.getUpdatedAt())
                .build();
    }

    /**
     * Maps MedicalRecord entity to MedicalRecordResponseDTO.
     */
    private MedicalRecordResponseDTO mapToMedicalRecordResponseDTO(MedicalRecord record) {
        return MedicalRecordResponseDTO.builder()
                .id(record.getId())
                .petId(record.getPet().getId())
                .petName(record.getPet().getName())
                .veterinarianId(record.getVeterinarianId())
                .visitDate(record.getVisitDate())
                .recordType(record.getRecordType())
                .diagnosis(record.getDiagnosis())
                .treatment(record.getTreatment())
                .medications(record.getMedications())
                .notes(record.getNotes())
                .weight(record.getWeight())
                .temperature(record.getTemperature())
                .heartRate(record.getHeartRate())
                .cost(record.getCost())
                .nextVisitDate(record.getNextVisitDate())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
