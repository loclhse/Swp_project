package com.example.Swp_Project.Controller;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.AppointmentDetail;
import com.example.Swp_Project.Service.AppointmentDetailService;
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
public class AppointmentDetailsController {
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    @GetMapping("/appointmentDetails/findAll")
    public ResponseEntity<List<AppointmentDetail>> getAllAppointmentDetails() {
        try {
            List<AppointmentDetail> appointmentDetails = appointmentDetailService.findAll();
            return ResponseEntity.ok(appointmentDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/appointmentsDetail/{appointmentId}/verify")
    public ResponseEntity<Appointment> updateAppointmentStatusToVerified(
            @PathVariable UUID appointmentId) {
        try {
            Appointment verifiedAppointment = appointmentDetailService.updateAppointmentStatusToVerified(appointmentId);
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

    @PutMapping("/appointmentsDetail/{appointmentId}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable UUID appointmentId) {
        try {
            Appointment newAppointment = appointmentDetailService.cancelAppointment(appointmentId);
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

    @PutMapping("/appointmentsDetail/{appointmentId}/mark-successful")
    public ResponseEntity<Appointment> markFinalDoseAsSuccessful(@PathVariable UUID appointmentId) {
        try {
            Appointment completedAppointment = appointmentDetailService.markAppointmentAsCompleted(appointmentId);
            return ResponseEntity.ok(completedAppointment);
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

    @DeleteMapping("/appointmentsDetail/{userId}/user")
    public ResponseEntity<String> deleteAppointmentsByUserId(@PathVariable UUID userId) {
        try {
            appointmentDetailService.deleteByUserId(userId);
            return ResponseEntity.ok("Appointments deleted successfully for user with ID: " + userId);
        } catch (NotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while deleting appointments.");
        }
    }

    @PutMapping("/appointmentDetails/{appointmentDetailId}/mark-paid")
    public ResponseEntity<AppointmentDetail> markAppointmentDetailAsPaid(
            @PathVariable UUID appointmentDetailId) {
        try {
            AppointmentDetail updatedAppointmentDetail = appointmentDetailService.updateStatusToPaid(appointmentDetailId);
            return ResponseEntity.ok(updatedAppointmentDetail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
