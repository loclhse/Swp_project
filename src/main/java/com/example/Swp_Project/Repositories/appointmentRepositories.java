package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface appointmentRepositories extends MongoRepository<Appointment, UUID> {
    List<Appointment> findByUserIdOrderByCreateAtDesc(UUID userId);
    Appointment findByAppointmentId(UUID appointmentId);
    boolean existsByUserIdAndVaccineDetailsListVaccineIdAndVaccineDetailsListCurrentDose(
            UUID userId,
            UUID vaccineId,
            Integer currentDose);
    List<Appointment> findByProcessId(UUID processId);
}
