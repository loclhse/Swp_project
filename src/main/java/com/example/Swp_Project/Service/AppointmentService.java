package com.example.Swp_Project.Service;
import com.example.Swp_Project.DTO.AppointmentDTO;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.AppointmentRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepositories appointmentRepository;


    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        if (appointment == null) {
            throw new NullPointerException("Appointment with ID " + appointmentId + " not found");
        }
        return appointment;
    }

    public List<Appointment> getAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserIdOrderByCreateAtDesc(userId);
    }

    public Appointment updateAppointment(UUID appointmentId, AppointmentDTO appointmentDTO) {
        Appointment appointmentt =appointmentRepository.findByAppointmentId (appointmentId);
        if (appointmentt == null) {
            throw new NullPointerException("there is no user with this id");
        }

        appointmentt.setChildrenName(appointmentDTO.getChildrenName());
        appointmentt.setNote(appointmentDTO.getNote());
        appointmentt.setMedicalIssue(appointmentDTO.getMedicalIssue());
        appointmentt.setChildrenGender(appointmentDTO.getChildrenGender());
        appointmentt.setDateOfBirth(appointmentDTO.getDateOfBirth());
        appointmentt.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointmentt.setTimeStart(appointmentDTO.getTimeStart());
        appointmentt.setUpdateAt(LocalDateTime.now());
        return appointmentRepository.save(appointmentt);
    }

    public void deleteAppointmentByUserId(UUID userId) {
        List<Appointment> notifications = appointmentRepository.findByUserIdOrderByCreateAtDesc(userId);
        if (notifications.isEmpty()) {
            throw new NullPointerException("No notifications found for user with ID: " + userId);
        }
        appointmentRepository.deleteAll(notifications);
    }

    public List<Appointment> getCompletedAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserIdAndStatus(userId,"Completed");
    }

}







