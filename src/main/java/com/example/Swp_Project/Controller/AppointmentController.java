package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.AppointmentDTO;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Repositories.UserRepositories;
import com.example.Swp_Project.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    @Autowired
    private AppointmentService appointmentService;


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
            @RequestBody AppointmentDTO appointment) {
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

    @DeleteMapping("/appointments/{userId}/user")
    public ResponseEntity<String> deleteAppointmentsByUserId(@PathVariable UUID userId) {
        try {
            appointmentService.deleteAppointmentByUserId(userId);
            return ResponseEntity.ok("Appointments deleted successfully for user with ID: " + userId);
        } catch (ExceptionInInitializerError ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting appointments.");
        }
    }

    @GetMapping("/appointments/{userId}/completed")
    public List<Appointment> getCompletedAppointmentsByUserId(@PathVariable UUID userId) {
        return appointmentService.getCompletedAppointmentsByUserId(userId);
    }

}

