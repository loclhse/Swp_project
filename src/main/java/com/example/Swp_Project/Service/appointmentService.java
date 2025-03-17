package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.childrenRepositories;
import com.example.Swp_Project.Repositories.notificationsRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import javax.management.Notification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class appointmentService {
    @Autowired
    private userRepositories userRepositories;
    @Autowired
    private appointmentRepositories appointmentRepository;
    @Autowired
    private childrenRepositories childrenRepositories;
@Autowired
private notificationsRepositories notificationsRepositories;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(UUID appointmentId) {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        if (appointment == null) {
            throw new NotFoundException("Appointment with ID " + appointmentId + " not found");
        }
        return appointment;
    }

    public List<Appointment> getAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserId(userId);
    }

    public Appointment createAppointment(UUID userId, appointmentDto appointmentDTO) {
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setChildrenName(appointmentDTO.getChildrenName());
        appointment.setNote(appointmentDTO.getNote());
        appointment.setMedicalIssue(appointmentDTO.getMedicalIssue());
        appointment.setChildrenGender(appointmentDTO.getChildrenGender());
        appointment.setDateOfBirth(appointmentDTO.getDateOfBirth());
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setVaccineDetailsList(new ArrayList<>());
        appointment.setTimeStart(appointmentDTO.getTimeStart());
        appointment.setStatus("Pending");
        appointment.setCreateAt(LocalDateTime.now());
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(UUID appointmentId, appointmentDto appointmentDTO) {
        Appointment appointmentt =appointmentRepository.findByAppointmentId (appointmentId);
        if (appointmentt == null) {
            throw new NullPointerException("there is no user with this id");
        }

        appointmentt.setChildrenName(appointmentDTO.getChildrenName());
        appointmentt.setNote(appointmentDTO.getNote());
        appointmentt.setMedicalIssue(appointmentDTO.getMedicalIssue());
        appointmentt.setChildrenGender(appointmentDTO.getChildrenGender());
        appointmentt.setDateOfBirth(appointmentDTO.getDateOfBirth());
        appointmentt.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointmentt.setTimeStart(appointmentDTO.getTimeStart());
        appointmentt.setUpdateAt(LocalDateTime.now());
        return appointmentRepository.save(appointmentt);
    }

    @Transactional
    public Appointment updateAppointmentStatusToVerified(UUID appointmentId) {

        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        if (appointment==null){
                throw new NotFoundException("Appointment not found with ID: " + appointmentId);
        }
        if (!"Pending".equals(appointment.getStatus())) {
            throw new IllegalStateException("Appointment must be in Pending status to verify");
        }

        appointment.setStatus("Verified Coming");
        appointment.setUpdateAt(LocalDateTime.now());

        createFollowUpAppointments(appointment);
        return appointmentRepository.save(appointment);

    }

    private void createFollowUpAppointments(Appointment originalAppointment) {
        List<VaccineDetails> vaccineDetailsList = originalAppointment.getVaccineDetailsList();

        for (VaccineDetails vaccine : vaccineDetailsList) {
            if (vaccine.getDoseRequire() != null && vaccine.getDoseRequire() > 1) {
                int dosesRemaining = vaccine.getDoseRequire() - 1;
                LocalDate nextAppointmentDate = originalAppointment.getAppointmentDate();

                for (int i = 0; i < dosesRemaining; i++) {
                    nextAppointmentDate = nextAppointmentDate.plusDays(vaccine.getDateBetweenDoses());

                    Appointment followingAppointment = new Appointment();
                    followingAppointment.setAppointmentId(UUID.randomUUID());
                    followingAppointment.setUserId(originalAppointment.getUserId());
                    followingAppointment.setChildrenName(originalAppointment.getChildrenName());
                    followingAppointment.setChildrenGender(originalAppointment.getChildrenGender());
                    followingAppointment.setDateOfBirth(originalAppointment.getDateOfBirth());
                    followingAppointment.setAppointmentDate(nextAppointmentDate);
                    followingAppointment.setTimeStart(originalAppointment.getTimeStart());
                    followingAppointment.setStatus("Pending");
                    followingAppointment.setCreateAt(LocalDateTime.now());
                    followingAppointment.setUpdateAt(LocalDateTime.now());

                    List<VaccineDetails> newVaccineList = new ArrayList<>();
                    VaccineDetails followingVaccine = new VaccineDetails();
                    followingVaccine.setVaccineId(vaccine.getVaccineId());
                    followingVaccine.setVaccineDetailsId(vaccine.getVaccineDetailsId());
                    followingVaccine.setDoseRequire(vaccine.getDoseRequire());
                    followingVaccine.setDoseName(vaccine.getDoseName() + " (Dose " + (i + 2) + ")");
                    followingVaccine.setManufacturer(vaccine.getManufacturer());
                    followingVaccine.setDateBetweenDoses(vaccine.getDateBetweenDoses());
                    followingVaccine.setPrice(vaccine.getPrice());
                    followingVaccine.setStatus(vaccine.getStatus());
                    newVaccineList.add(followingVaccine);
                    followingAppointment.setVaccineDetailsList(newVaccineList);
                    Appointment savedAppointment = appointmentRepository.save(followingAppointment);
                    createNotification(savedAppointment);

                }
            }
        }
    }
    private void createNotification(Appointment appointment) {
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setTitle("Dear Customer.");
        notification.setUserID(appointment.getUserId());
        notification.setMessages(String.format(
                "A new following appointment has been scheduled for %s on %s at %s for %s",
                appointment.getChildrenName(),
                appointment.getAppointmentDate().toString(),
                appointment.getTimeStart().toString(),
                appointment.getVaccineDetailsList().get(0).getDoseName()
        ));
        notification.setCreatedAt(LocalDateTime.now());

        notificationsRepositories.save(notification);
    }

}





