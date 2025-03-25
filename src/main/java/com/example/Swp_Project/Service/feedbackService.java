package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.feedbackDto;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Feedback createFeedback(UUID userId, UUID appointmentId, feedbackDto feedbackdto) throws Exception {

        Optional<User> userOpt = userRepositories.findById(userId);
        if (userOpt.isEmpty()) {
            throw new Exception("User not found for ID: " + userId);
        }
        User user = userOpt.get();


        Optional<Appointment> appointmentOpt = appointmentRepositories.findById(appointmentId);
        if (appointmentOpt.isEmpty()) {
            throw new Exception("Appointment not found for ID: " + appointmentId);
        }

        Feedback feedback = new Feedback();
        feedback.setFeedbackId(UUID.randomUUID());
        feedback.setUserId(userId);
        feedback.setUsername(user.getUsername());
        feedback.setAppointmentsId(appointmentId);
        feedback.setRating(feedbackdto.getRating());
        feedback.setContext(feedbackdto.getContext());
        feedback.setCreateAt(LocalDateTime.now());
        return feedbackRepository.save(feedback);
    }

    public Feedback getFeedbackById(UUID feedbackId) {
        return feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found with ID: " + feedbackId));
    }

    public List<Feedback> getUserFeedbacks(UUID userId) {
        List<Feedback> feedbacks = feedbackRepository.findByUserId(userId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new RuntimeException("No feedback found for user with ID: " + userId);
        }
        return feedbacks;
    }

    public List<Feedback> getAppointmentFeedbacks(UUID appointmentId) {
        List<Feedback> feedbacks = feedbackRepository.findByAppointmentsId(appointmentId);
        if (feedbacks == null || feedbacks.isEmpty()) {
            throw new RuntimeException("No feedback found for appointment with ID: " + appointmentId);
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
        updatedFeedback.setUpdateAt(LocalDateTime.now());
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

