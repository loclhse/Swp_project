package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Staff;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface staffRepositories extends MongoRepository<Staff, UUID> {
    Optional<Staff>findByEmail(String email);
}
