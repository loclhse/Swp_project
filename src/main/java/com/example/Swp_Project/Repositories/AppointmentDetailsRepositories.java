package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.AppointmentDetail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface AppointmentDetailsRepositories extends MongoRepository<AppointmentDetail, UUID> {
  List<AppointmentDetail> findByUserId(UUID userId);
  AppointmentDetail findByAppointmentDetailId(UUID appointmentDetailId);
}

