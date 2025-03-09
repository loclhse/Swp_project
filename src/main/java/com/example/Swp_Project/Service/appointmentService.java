package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class appointmentService {
    @Autowired
    private userRepositories userRepositories;
    @Autowired
    private appointmentRepositories appointmentRepositories;
    public Appointment createAppointment(UUID userID, Appointment appointment) {
        User user = userRepositories.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, dawg!"));
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setUserId(userID);
        appointment.setStatus("pending");
        appointment.setCreateAt(LocalDateTime.now());
        user.getAppointments().add(appointment); // Add to user's appointments list
        userRepositories.save(user); // Save user with updated list
        return appointmentRepositories.save(appointment); // Save appointment
    }
    public Appointment getAppointmentById(UUID appointmentId) {
        return appointmentRepositories.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found, yo!"));
    }
    public List<Appointment> getUserAppointments(UUID userID) {
        User user = userRepositories.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, dawg!"));
        return user.getAppointments();
    }
    public Appointment updateAppointment(UUID appointmentId, Appointment updatedAppointment) {
        Appointment existing = getAppointmentById(appointmentId);
        existing.setChildrenName(updatedAppointment.getChildrenName()); // Now updating childrenName
        existing.setParentName(updatedAppointment.getParentName());
        existing.setAppointmentDate(updatedAppointment.getAppointmentDate());
        existing.setTimeStart(updatedAppointment.getTimeStart());
        existing.setFeedbacks(updatedAppointment.getFeedbacks());
        existing.setVaccineDetailsList(updatedAppointment.getVaccineDetailsList());
        existing.setStatus(updatedAppointment.getStatus());

        User user = userRepositories.findById(existing.getUserId()).get();
        user.getAppointments().removeIf(a -> a.getAppointmentId().equals(appointmentId));
        user.getAppointments().add(existing);
        userRepositories.save(user);

        return appointmentRepositories.save(existing);
    }
    public void deleteAppointment(UUID userID, UUID appointmentId) {
        User user = userRepositories.findById(userID)
                .orElseThrow(() -> new RuntimeException("User not found, dawg!"));
        Appointment appointment = getAppointmentById(appointmentId);
        user.getAppointments().removeIf(a -> a.getAppointmentId().equals(appointmentId));
        userRepositories.save(user);
        appointmentRepositories.delete(appointment);
    }
}
