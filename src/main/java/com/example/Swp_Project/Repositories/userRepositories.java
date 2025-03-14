package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface userRepositories extends MongoRepository<User, UUID> {
Optional<User> findByEmail(String email);
Optional<User>findByUsername(String username);
User findByUserID(UUID userID);
}
