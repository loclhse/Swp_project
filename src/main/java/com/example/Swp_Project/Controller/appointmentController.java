package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.appointmentDto;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Repositories.userRepositories;
import com.example.Swp_Project.Service.appointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class appointmentController {

    @Autowired
    private appointmentService appointmentService;
    @Autowired
    private userRepositories userRepositories;

    @GetMapping("/appointments-all")
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable UUID appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }


    @GetMapping("/appointment-getbyuserid/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByUserId(@PathVariable UUID userId) {
        List<Appointment> appointmentList = appointmentService.getAppointmentsByUserId(userId);
        if (appointmentList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointmentList);
    }

    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable UUID appointmentId,
            @RequestBody appointmentDto appointment) {
        try {
            if (appointmentId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentId, appointment);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/appointments/{appointmentId}/verify")
    public ResponseEntity<Appointment> updateAppointmentStatusToVerified(
            @PathVariable UUID appointmentId) {
        try {
            Appointment verifiedAppointment = appointmentService.updateAppointmentStatusToVerified(appointmentId);
            return ResponseEntity.ok(verifiedAppointment);
        } catch (NotFoundException e) {
            System.out.println("Not Found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            System.out.println("Conflict: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/appointments/{appointmentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable UUID appointmentId) {
        try {
            Appointment newAppointment = appointmentService.cancelAppointment(appointmentId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Appointment canceled successfully. A Vaccine now being stored in Your Vaccine.");
            response.put("appointment canceled: ", newAppointment);

            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }

    @DeleteMapping("/appointments/user/{userId}")
    public ResponseEntity<String> deleteAppointmentsByUserId(@PathVariable UUID userId) {
        try {
            appointmentService.deleteAppointmentByUserId(userId);
            return ResponseEntity.ok("Appointments deleted successfully for user with ID: " + userId);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting appointments.");
        }
    }

    @PostMapping("/appointments/{appointmentId}/mark-successful")
    public ResponseEntity<Void> markFinalDoseAsSuccessful(@PathVariable UUID appointmentId) {
        appointmentService.markAppointmentAsCompleted(appointmentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/appointments/{userId}/completed")
    public List<Appointment> getCompletedAppointmentsByUserId(@PathVariable UUID userId) {
        return appointmentService.getCompletedAppointmentsByUserId(userId);
    }

}

