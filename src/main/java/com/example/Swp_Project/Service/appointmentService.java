package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.appointmentDto;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.childrenRepositories;
import com.example.Swp_Project.Repositories.notificationsRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
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
            createFollowUpAppointments(appointment);
            appointment.setUpdateAt(LocalDateTime.now());
            Appointment savedAppointment = appointmentRepository.save(appointment);
            createFollowUpAppointments(savedAppointment);
            notifyUserAboutNextAppointment(savedAppointment);
            return appointmentRepository.save(savedAppointment);
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
                    nextAppointmentVaccine.setDoseName(vaccine.getDoseName() + " (Dose " + (vaccine.getCurrentDose() + 1)+ ")");
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
            throw new IllegalArgumentException("Invalid appointment provided for notification");
        }

        LocalDate today = LocalDate.now();
        LocalDate appointmentDate = appointment.getAppointmentDate();
        long daysUntilAppointment = ChronoUnit.DAYS.between(today, appointmentDate);
        String title;
        String message;

        if (daysUntilAppointment <= 1) {
            title = "Urgent: Appointment Tomorrow";
            message = "Your vaccination appointment for " + appointment.getChildrenName() +
                    " is scheduled for tomorrow, " + appointmentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        } else if (daysUntilAppointment <= 7) {
            title = "Upcoming Appointment";
            message = "Your vaccination appointment for " + appointment.getChildrenName() +
                    " is scheduled in " + daysUntilAppointment + " days, on " +
                    appointmentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        } else {
            title = "Scheduled Appointment";
            message = "Your next vaccination appointment for " + appointment.getChildrenName() +
                    " is scheduled for " + appointmentDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        }
        if (appointment.getVaccineDetailsList() != null && !appointment.getVaccineDetailsList().isEmpty()) {
            message += ". Vaccines scheduled: ";
            List<String> vaccineNames = new ArrayList<>();
            for (VaccineDetails vaccine : appointment.getVaccineDetailsList()) {
                vaccineNames.add(vaccine.getDoseName());
            }
            message += String.join(", ", vaccineNames);
        }
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setUserID(appointment.getUserId());
        notification.setTitle(title);
        notification.setMessages(message);
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


}







