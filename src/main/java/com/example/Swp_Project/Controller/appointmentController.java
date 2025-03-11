package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Service.appointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
public class appointmentController {

    @Autowired
    private appointmentService appointmentService;

    @PostMapping("/{userID}")
    public Appointment createAppointment(@PathVariable UUID userID, @RequestBody Appointment appointment) {
        return appointmentService.createAppointment(userID, appointment);
    }

    @GetMapping("/{appointmentId}")
    public Appointment getAppointment(@PathVariable UUID appointmentId) {
        return appointmentService.getAppointmentById(appointmentId);
    }

    @GetMapping("/{userID}/user")
    public List<Appointment> getUserAppointments(@PathVariable UUID userID) {
        return appointmentService.getUserAppointments(userID);
    }

    @PutMapping("/{appointmentId}")
    public Appointment updateAppointment(@PathVariable UUID appointmentId, @RequestBody Appointment appointment) {
        return appointmentService.updateAppointment(appointmentId, appointment);
    }

    @DeleteMapping("/{userID}/{appointmentId}")
    public void deleteAppointment(@PathVariable UUID userID, @PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(userID, appointmentId);
    }
}
