package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Appointment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentRepositories extends MongoRepository<Appointment, UUID> {
    List<Appointment> findByUserIdOrderByCreateAtDesc(UUID userId);
    Appointment findByAppointmentId(UUID appointmentId);
    boolean existsByUserIdAndVaccineDetailsListVaccineIdAndVaccineDetailsListCurrentDoseAndVaccineDetailsListVaccinationSeriesId(
            UUID userId,
            UUID vaccineId,
            Integer currentDose,
            UUID seriesId
    );
    List<Appointment> findByUserIdAndStatus(UUID userId, String status);

}
