package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Vaccin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface VaccineRepositories extends MongoRepository<Vaccin, UUID> {
     Optional<Vaccin> findByIllnessName(String illnessName);
     Optional<Vaccin>findById(UUID vaccineId);
}
