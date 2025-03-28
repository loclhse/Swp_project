package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepositories extends MongoRepository<User, UUID> {
Optional<User> findByEmail(String email);
Optional<User>findByUsername(String username);
User findByUserID(UUID userID);
}
