package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Notifications;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationsRepositories extends MongoRepository<Notifications,UUID> {
    List<Notifications>findAllByOrderByCreatedAtDesc();
    Optional<Notifications>findById(UUID id);
    List<Notifications>findByUserID(UUID userID);

}
