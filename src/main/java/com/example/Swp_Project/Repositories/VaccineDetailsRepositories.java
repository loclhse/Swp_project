package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.VaccineDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface VaccineDetailsRepositories extends MongoRepository<VaccineDetails, UUID> {
   VaccineDetails findByDoseName(String doseName);
   List<VaccineDetails>findByVaccineId(UUID vaccineId);
}
