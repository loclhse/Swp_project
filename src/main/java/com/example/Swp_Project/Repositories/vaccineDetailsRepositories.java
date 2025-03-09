package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Vaccin;
import com.example.Swp_Project.Model.VaccineDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface vaccineDetailsRepositories extends MongoRepository<VaccineDetails, UUID> {
   VaccineDetails findByDoseName(String doseName);
}
