package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface appointmentRepositories extends MongoRepository<Appointment, UUID> {
    List<Appointment>findByUserId(UUID userId);
    Appointment findByAppointmentId(UUID appointmentId);
    List<Appointment> findByStatus(String status);
}
