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
        return appointmentRepository.findByUserIdOrderByCreateAtDesc(userId);
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

    @Transactional
    public Appointment cancelAppointment(UUID appointmentId) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        if (!"Pending".equals(appointment.getStatus())) {
            throw new IllegalStateException("Appointment must be in Pending status to cancel");
        }

        appointment.setStatus("Canceled");
        appointment.setUpdateAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        VaccineStorage vaccineStorage = new VaccineStorage();
        vaccineStorage.setUserId(appointment.getUserId());
        vaccineStorage.setVaccineDetailsStorage(appointment.getVaccineDetailsList());
        vaccineStorage.setCreatAt(LocalDateTime.now());
        createNotificationForCancel(appointment);
        return appointment;
    }

    @Transactional
    public void markFinalDoseAsSuccessful(UUID appointmentId) {
        Appointment finalDoseAppointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + appointmentId));

        if (!"Pending".equals(finalDoseAppointment.getStatus())) {
            throw new IllegalStateException("Appointment must be in Pending status to mark as successful");
        }

        finalDoseAppointment.setStatus("Completed");
        finalDoseAppointment.setUpdateAt(LocalDateTime.now());
        appointmentRepository.save(finalDoseAppointment);

        List<Appointment> relatedAppointments = appointmentRepository.findByProcessId(finalDoseAppointment.getAppointmentId());
        for (Appointment appointment : relatedAppointments) {
            appointment.setStatus("Completed");
            appointment.setUpdateAt(LocalDateTime.now());
            appointmentRepository.save(appointment);
        }
        createNotification(finalDoseAppointment);
        for (Appointment appointment : relatedAppointments) {
            createNotificationforCompleted(appointment);
        }
    }

    private void createFollowUpAppointments(Appointment originalAppointment) {
        List<VaccineDetails> vaccineDetailsList = originalAppointment.getVaccineDetailsList();

        for (VaccineDetails vaccine : vaccineDetailsList) {
            if (vaccine.getDoseRequire() != null && vaccine.getCurrentDose() != null
                    && vaccine.getCurrentDose() < vaccine.getDoseRequire()) {

                int nextDose = vaccine.getCurrentDose() + 1;

                if (appointmentRepository.existsByUserIdAndVaccineDetailsListVaccineIdAndVaccineDetailsListCurrentDose(
                        originalAppointment.getUserId(), vaccine.getVaccineId(), nextDose)) {
                    continue;
                }
                    LocalDate nextAppointmentDate = originalAppointment.getAppointmentDate().plusDays(vaccine.getDateBetweenDoses());
                    Appointment followingAppointment = new Appointment();
                    followingAppointment.setAppointmentId(UUID.randomUUID());
                    followingAppointment.setUserId(originalAppointment.getUserId());
                    followingAppointment.setProcessId(originalAppointment.getProcessId());
                    followingAppointment.setChildrenName(originalAppointment.getChildrenName());
                    followingAppointment.setChildrenGender(originalAppointment.getChildrenGender());
                    followingAppointment.setDateOfBirth(originalAppointment.getDateOfBirth());
                    followingAppointment.setAppointmentDate(nextAppointmentDate);
                    followingAppointment.setTimeStart(null);
                    followingAppointment.setCreateAt(LocalDateTime.now());
                    followingAppointment.setStatus("Pending");
                    followingAppointment.setFinalDose(nextDose == vaccine.getDoseRequire());

                    List<VaccineDetails> newVaccineList = new ArrayList<>();
                    VaccineDetails nextAppointmentVaccine = new VaccineDetails();
                    nextAppointmentVaccine.setVaccineId(vaccine.getVaccineId());
                    nextAppointmentVaccine.setVaccineDetailsId(vaccine.getVaccineDetailsId());
                    nextAppointmentVaccine.setDoseRequire(vaccine.getDoseRequire());
                    nextAppointmentVaccine.setDoseName(vaccine.getDoseName() + " (Dose " + (vaccine.getCurrentDose() + 1)+ ")");
                    nextAppointmentVaccine.setManufacturer(vaccine.getManufacturer());
                    nextAppointmentVaccine.setDateBetweenDoses(vaccine.getDateBetweenDoses());
                    nextAppointmentVaccine.setPrice(vaccine.getPrice());
                    nextAppointmentVaccine.setStatus(vaccine.getStatus());
                    nextAppointmentVaccine.setCurrentDose(vaccine.getCurrentDose() + 1);
                    newVaccineList.add(nextAppointmentVaccine);
                    followingAppointment.setVaccineDetailsList(newVaccineList);
                    Appointment savedAppointment = appointmentRepository.save(followingAppointment);
                    createNotification(savedAppointment);
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

    private void createNotificationForCancel(Appointment appointment) {
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setTitle("Dear Customer.");
        notification.setUserID(appointment.getUserId());
        notification.setMessages(String.format(
                "The vaccine %s has been stored at %s",
                appointment.getVaccineDetailsList().get(0).getDoseName(),
                appointment.getCreateAt().toString()
        ));
        notification.setCreatedAt(LocalDateTime.now());

        notificationsRepositories.save(notification);
    }

    public void deleteAppointmentByUserId(UUID userId) {
        List<Appointment> notifications = appointmentRepository.findByUserIdOrderByCreateAtDesc(userId);
        if (notifications.isEmpty()) {
            throw new NotFoundException("No notifications found for user with ID: " + userId);
        }
        appointmentRepository.deleteAll(notifications);
    }

    private void createNotificationforCompleted(Appointment appointment) {
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setTitle("Dear Customer.");
        notification.setUserID(appointment.getUserId());
        notification.setMessages(String.format(
                "You have now completed all dose of %s",
                appointment.getVaccineDetailsList().get(0).getDoseName()
        ));
        notification.setCreatedAt(LocalDateTime.now());

        notificationsRepositories.save(notification);
    }
}







