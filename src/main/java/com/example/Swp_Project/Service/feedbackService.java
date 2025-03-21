package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.Feedback;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.feedbackRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class feedbackService {
@Autowired
    private feedbackRepositories feedbackRepository;
@Autowired
    private userRepositories userRepositories;
@Autowired
    private appointmentRepositories appointmentRepositories;

    public List<Feedback>getAllFeedback(){
       return feedbackRepository.findAll();
    }

    public ResponseEntity<String> addFeedback(UUID userId, UUID appointmentId, Feedback feedback) {
        Optional<User> userOpt = userRepositories.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        Optional<Appointment> appointmentOpt = appointmentRepositories.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Appointment not found.");
        }
        User user = userOpt.get();
           Appointment appointment = appointmentOpt.get();
           feedback.setFeedbackId(UUID.randomUUID());
           feedback.setUserId(userId);
           feedback.setAppointmentsId(appointmentId);

        user.getFeedbacks().add(feedback);
        appointment.getFeedbacks().add(feedback);

        userRepositories.save(user);
        appointmentRepositories.save(appointment);
        feedbackRepository.save(feedback);

        return ResponseEntity.ok("Feedback submitted successfully.");
    }

    public Feedback getFeedbackById(UUID feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId + ", fam!"));
    }

    public List<Feedback> getUserFeedbacks(UUID userId) {
        User user = userRepositories.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId + ", fam!"));
        List<Feedback> feedbacks = user.getFeedbacks();
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new RuntimeException("No feedback found for user with ID: " + userId + ", dawg!");
        }
        return feedbacks;
    }

    public List<Feedback> getAppointmentFeedbacks(UUID appointmentId) {
        Appointment appointment = appointmentRepositories.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId + ", fam!"));
        List<Feedback> feedbacks = appointment.getFeedbacks();
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new RuntimeException("No feedback found for appointment with ID: " + appointmentId + ", dawg!");
        }
        return feedbacks;
    }
    public Feedback updateFeedback(UUID userId, UUID appointmentId, UUID feedbackId, Feedback updatedFeedback) {
        User user = userRepositories.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId + ", fam!"));
        Appointment appointment = appointmentRepositories.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId + ", fam!"));


        Feedback userFeedback = user.getFeedbacks().stream()
                .filter(f -> f.getFeedbackId().equals(feedbackId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId + ", dawg!"));
        Feedback appointmentFeedback = appointment.getFeedbacks().stream()
                .filter(f -> f.getFeedbackId().equals(feedbackId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId + ", dawg!"));

        if (updatedFeedback.getContext() != null) {
            userFeedback.setContext(updatedFeedback.getContext());
            appointmentFeedback.setContext(updatedFeedback.getContext());
        }
        if (updatedFeedback.getRating() != null) {
            userFeedback.setRating(updatedFeedback.getRating());
            appointmentFeedback.setRating(updatedFeedback.getRating());
        }
        userRepositories.save(user);
        appointmentRepositories.save(appointment);
        Feedback savedFeedback = feedbackRepository.save(userFeedback);
        return savedFeedback;
    }
    public void deleteFeedback(UUID userId, UUID appointmentId, UUID feedbackId) {
        User user = userRepositories.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId + ", fam!"));
        Appointment appointment = appointmentRepositories.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + appointmentId + ", fam!"));

        boolean userRemoved = user.getFeedbacks().removeIf(f -> f.getFeedbackId().equals(feedbackId));
        if (!userRemoved) {
            throw new RuntimeException("Feedback not found in User's list with ID: " + feedbackId + ", dawg!");
        }
        boolean apptRemoved = appointment.getFeedbacks().removeIf(f -> f.getFeedbackId().equals(feedbackId));
        if (!apptRemoved) {
            throw new RuntimeException("Feedback not found in Appointment's list with ID: " + feedbackId + ", dawg!");
        }
        feedbackRepository.deleteById(feedbackId);
        userRepositories.save(user);
        appointmentRepositories.save(appointment);
    }







}

