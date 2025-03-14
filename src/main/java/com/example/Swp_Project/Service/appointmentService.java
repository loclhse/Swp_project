package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class appointmentService {
    @Autowired
    private userRepositories userRepositories;
    @Autowired
    private appointmentRepositories appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> getAppointmentById(UUID appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    public List<Appointment> getAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public Appointment createAppointment(UUID userId,appointmentDto appointmentDTO) {
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setChildrenName(appointmentDTO.getChildrenName());
        appointment.setNote(appointmentDTO.getNote());
        appointment.setMedicalIssue(appointmentDTO.getMedicalIssue());
        appointment.setChildrenGender(appointmentDTO.getChildrenGender());
        appointment.setDateOfBirth(appointmentDTO.getDateOfBirth());
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setTimeStart(appointmentDTO.getTimeStart());
        appointment.setCreateAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

}
