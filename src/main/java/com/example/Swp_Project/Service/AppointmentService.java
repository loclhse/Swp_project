package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.AppointmentDTO;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.AppointmentRepositories;
import com.example.Swp_Project.Repositories.ChildrenRepositories;
import com.example.Swp_Project.Repositories.NotificationsRepositories;
import com.example.Swp_Project.Repositories.UserRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private AppointmentRepositories appointmentRepository;
    @Autowired
    private ChildrenRepositories childrenRepositories;
    @Autowired
    private NotificationsRepositories notificationsRepositories;
    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

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

    public Appointment updateAppointment(UUID appointmentId, AppointmentDTO appointmentDTO) {
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

        appointment.setStatus("Verified Coming");
        appointment.setUpdateAt(LocalDateTime.now());
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
        return appointment;
    }

        @Transactional
        public Appointment markAppointmentAsCompleted(UUID appointmentId) {
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            if (appointment == null) {
                throw new NotFoundException("Appointment not found with ID: " + appointmentId);
            }

            if (!"Verified Coming".equals(appointment.getStatus())) {
                throw new IllegalStateException("Appointment must be in 'Verified Coming' status to mark as completed");
            }

            appointment.setStatus("Completed");
            appointment.setUpdateAt(LocalDateTime.now());
            Appointment savedAppointment = appointmentRepository.save(appointment);
            try {
                createFollowUpAppointments(savedAppointment);
                notifyUserAboutNextAppointment(savedAppointment);
                return savedAppointment;
            } catch (Exception e) {
                System.err.println("Error processing appointment: " + e.getMessage());
                e.printStackTrace();
                return null;
            }


        }

    private void createFollowUpAppointments(Appointment originalAppointment) {
        List<VaccineDetails> vaccineDetailsList = originalAppointment.getVaccineDetailsList();

        for (VaccineDetails vaccine : vaccineDetailsList) {
            if (vaccine.getDoseRequire() != null && vaccine.getCurrentDose() != null
                    && vaccine.getCurrentDose() < vaccine.getDoseRequire()) {

                int nextDose = vaccine.getCurrentDose() + 1;
                UUID seriesId=vaccine.getVaccinationSeriesId();

                if (appointmentRepository.existsByUserIdAndVaccineDetailsListVaccineIdAndVaccineDetailsListCurrentDoseAndVaccineDetailsListVaccinationSeriesId(
                        originalAppointment.getUserId(), vaccine.getVaccineId(), nextDose, seriesId)) {
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
                    nextAppointmentVaccine.setVaccinationSeriesId(vaccine.getVaccinationSeriesId());
                    nextAppointmentVaccine.setDoseRequire(vaccine.getDoseRequire());
                    nextAppointmentVaccine.setDoseName(vaccine.getDoseName());
                    nextAppointmentVaccine.setManufacturer(vaccine.getManufacturer());
                    nextAppointmentVaccine.setDateBetweenDoses(vaccine.getDateBetweenDoses());
                    nextAppointmentVaccine.setPrice(vaccine.getPrice());
                    nextAppointmentVaccine.setStatus(vaccine.getStatus());
                    nextAppointmentVaccine.setCurrentDose(vaccine.getCurrentDose() + 1);
                    newVaccineList.add(nextAppointmentVaccine);
                    followingAppointment.setVaccineDetailsList(newVaccineList);
                    appointmentRepository.save(followingAppointment);

              }
            }
        }

    @Transactional
    public void notifyUserAboutNextAppointment(Appointment appointment) {

        if (appointment == null || appointment.getAppointmentDate() == null) {
            logger.warn("Invalid appointment: null or missing appointment date");
            return;
        }

        Integer dateBetweenDoses = (appointment.getVaccineDetailsList() == null ||
                appointment.getVaccineDetailsList().isEmpty())
                ? null
                : appointment.getVaccineDetailsList().get(0).getDateBetweenDoses();

        if (dateBetweenDoses == null) {
            logger.warn("No valid date between doses found");
            return;
        }

        LocalDate today = appointment.getAppointmentDate();
        LocalDate nextAppointmentDate = appointment.getAppointmentDate().plusDays(dateBetweenDoses);

        if (nextAppointmentDate.isBefore(today)) {
            nextAppointmentDate = today.plusDays(dateBetweenDoses);
        }


        long daysUntilNextAppointment = ChronoUnit.DAYS.between(today, nextAppointmentDate);

        if (daysUntilNextAppointment <= 0) {
            logger.warn("Cannot create notification: Next appointment date calculation issue");
            return;
        }
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setUserID(appointment.getUserId());
        notification.setTitle("Next Appointment Reminder");
        notification.setMessages(
                "Your next vaccination appointment for " + appointment.getChildrenName() +
                        " is scheduled " + dateBetweenDoses + " days after the previous appointment, " +
                        "which falls on " + nextAppointmentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"))
        );
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

    public List<Appointment> getCompletedAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserIdAndStatus(userId,"Completed");
    }

}







