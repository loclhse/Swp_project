package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Service.appointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointment")
public class appointmentController {

    @Autowired
    private appointmentService appointmentService;


    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Appointment>> getAppointmentById(@PathVariable(value = "id") UUID appointmentId) {
        Optional<Appointment> appointment = appointmentService.getAppointmentById(appointmentId);
        if (appointment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByUserId(@PathVariable(value = "userId") UUID userId) {
        List<Appointment> appointmentList = appointmentService.getAppointmentsByUserId(userId);
        if (appointmentList.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(appointmentList);
    }

    @PostMapping
    public Appointment createAppointment(@PathVariable(value = "userId") UUID userid, @RequestBody appointmentDto appointmentDTO) {
        return appointmentService.createAppointment(userid,appointmentDTO);
    }



}
