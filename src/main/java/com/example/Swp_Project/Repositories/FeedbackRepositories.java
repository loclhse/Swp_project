package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepositories extends MongoRepository<Feedback,UUID> {

List<Feedback>findAllByOrderByCreateAtDesc();
List<Feedback>findByAppointmentsId(UUID appointmentsId);
List<Feedback>findByUserId(UUID userId);

}
