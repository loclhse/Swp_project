package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.UUID;

public interface adminRepositories extends MongoRepository<Admin, UUID> {
    Optional<Admin>findByEmail(String email);
}
