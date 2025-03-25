package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Feedback;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface feedbackRepositories extends MongoRepository<Feedback,UUID> {

List<Feedback>findAllByOrderByCreateAtDesc();
List<Feedback>findByAppointmentsId(UUID appointmentsId);
List<Feedback>findByUserId(UUID userId);

}
