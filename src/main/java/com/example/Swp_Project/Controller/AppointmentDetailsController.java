package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.ReactionDTO;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.AppointmentDetail;
import com.example.Swp_Project.Service.AppointmentDetailService;
import com.example.Swp_Project.Service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class AppointmentDetailsController {
    @Autowired
    private AppointmentDetailService appointmentDetailService;
    private static final Logger logger = LoggerFactory.getLogger(InjectionHistoryController.class);

    @GetMapping("/appointmentDetails/findAll")
    public ResponseEntity<List<AppointmentDetail>> getAllAppointmentDetails() {
        try {
            List<AppointmentDetail> appointmentDetails = appointmentDetailService.findAll();
            return ResponseEntity.ok(appointmentDetails);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PostMapping("/appointmentDetails/{appointmentId}/record-reaction-and-set-qualification")
    public ResponseEntity<Map<String, Object>> recordReactionAndSetQualification(
            @PathVariable UUID appointmentId,
            @RequestBody ReactionDTO requestBody) {
        try {
            Appointment appointment = appointmentDetailService.recordReactionAndSetQualification(
                    appointmentId, requestBody.getCondition(), requestBody.isQualified());
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Reaction recorded and qualification status updated");
            successResponse.put("appointment", appointment);
            successResponse.put("isOkay", appointment.isOkay());
            return ResponseEntity.ok(successResponse);
        } catch (NullPointerException e) {
            logger.error("Not found error: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            logger.error("Illegal state error: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "IllegalState");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/appointmentsDetail/{appointmentId}/verify")
    public ResponseEntity<Appointment> updateAppointmentStatusToVerified(
            @PathVariable UUID appointmentId) {
        try {
            Appointment verifiedAppointment = appointmentDetailService.updateAppointmentStatusToVerified(appointmentId);
            return ResponseEntity.ok(verifiedAppointment);
        } catch (IllegalArgumentException e) {
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
        } catch (IllegalArgumentException e) {
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
            System.out.println("Received request to mark appointment as successful for ID: " + appointmentId);
            Appointment completedAppointment = appointmentDetailService.markAppointmentAsCompleted(appointmentId);
            System.out.println("Successfully marked appointment as completed: " + completedAppointment);
            return ResponseEntity.ok(completedAppointment);
        } catch (IllegalArgumentException e) {
            System.out.println("Not Found: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalStateException e) {
            System.out.println("Conflict: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/appointmentsDetail/{userId}/user")
    public ResponseEntity<String> deleteAppointmentsByUserId(@PathVariable UUID userId) {
        try {
            appointmentDetailService.deleteByUserId(userId);
            return ResponseEntity.ok("Appointments deleted successfully for user with ID: " + userId);
        } catch (IllegalStateException ex) {
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
