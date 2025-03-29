package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.AppointmentDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentDetailsRepositories extends MongoRepository<AppointmentDetail, UUID> {
  List<AppointmentDetail> findByUserId(UUID userId);
  Optional<AppointmentDetail> findByAppointmentDetailId(UUID appointmentDetailId);
}

