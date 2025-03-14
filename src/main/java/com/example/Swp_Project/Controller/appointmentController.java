package com.example.Swp_Project.Controller;
import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Repositories.userRepositories;
import com.example.Swp_Project.Service.appointmentService;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
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


    @GetMapping("/users/{userId}/appointments")
    public ResponseEntity<List<Appointment>> getAppointmentsByUserId(@PathVariable UUID userId) {
        List<Appointment> appointmentList = appointmentService.getAppointmentsByUserId(userId);
        if (appointmentList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointmentList);
    }

    @PostMapping("/appointments/users/{userId}")
    public ResponseEntity<Appointment> createAppointment(
            @PathVariable UUID userId, // Changed from @RequestParam to @PathVariable for consistency
            @RequestBody appointmentDto appointmentDto) { // Assuming "appointmentDto" should be "AppointmentDto"
        try {
            if (userId == null) {
                return ResponseEntity.badRequest().body(null);
            }
            Appointment createdAppointment = appointmentService.createAppointment(userId, appointmentDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (MongoException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
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

}

