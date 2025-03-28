package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface AdminRepositories extends MongoRepository<Admin, UUID> {
    Optional<Admin>findByEmail(String email);
}
