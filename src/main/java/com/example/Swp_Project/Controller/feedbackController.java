package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Feedback;
import com.example.Swp_Project.Service.feedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
public class feedbackController {

    @Autowired
    private feedbackService feedbackService;

    @PostMapping("/users/{userId}/appointments/{appointmentId}/feedback")
    public ResponseEntity<String> addFeedback(
            @PathVariable UUID userId,
            @PathVariable UUID appointmentId,
            @RequestBody Feedback feedback) {
        return feedbackService.addFeedback(userId, appointmentId, feedback);
    }

    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable UUID feedbackId) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(feedbackId);
            return ResponseEntity.ok(feedback);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/users/{userId}/feedback")
    public ResponseEntity<List<Feedback>> getUserFeedbacks(@PathVariable UUID userId) {
        try {
            List<Feedback> feedbacks = feedbackService.getUserFeedbacks(userId);
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/appointments/{appointmentId}/feedback")
    public ResponseEntity<List<Feedback>> getAppointmentFeedbacks(@PathVariable UUID appointmentId) {
        try {
            List<Feedback> feedbacks = feedbackService.getAppointmentFeedbacks(appointmentId);
            return ResponseEntity.ok(feedbacks);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/users/{userId}/appointments/{appointmentId}/feedback/{feedbackId}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable UUID userId,
            @PathVariable UUID appointmentId,
            @PathVariable UUID feedbackId,
            @RequestBody Feedback updatedFeedback) {
        try {
            Feedback updated = feedbackService.updateFeedback(userId, appointmentId, feedbackId, updatedFeedback);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("/users/{userId}/appointments/{appointmentId}/feedback/{feedbackId}")
    public ResponseEntity<String> deleteFeedback(
            @PathVariable UUID userId,
            @PathVariable UUID appointmentId,
            @PathVariable UUID feedbackId) {
        try {
            feedbackService.deleteFeedback(userId, appointmentId, feedbackId);
            return ResponseEntity.ok("Feedback deleted, fam!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}

