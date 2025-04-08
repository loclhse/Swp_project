package com.example.Swp_Project.Service;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AppointmentDetailService {
@Autowired
private AppointmentDetailsRepositories appointmentDetailsRepositories;
@Autowired
private AppointmentRepositories appointmentRepositories;
@Autowired
private NotificationsRepositories notificationsRepositories;
@Autowired
private PaymentsRepositories paymentsRepositories;
@Autowired
private ChildrenRepositories childrenRepositories;
@Autowired
private InjectionHistoryRepositories injectionHistoryRepositories;
private final static Logger logger= LoggerFactory.getLogger(AppointmentDetailService.class);

    public List<AppointmentDetail>findAll(){
    return appointmentDetailsRepositories.findAll();
    }

   @Transactional
    public Appointment updateAppointmentStatusToVerified(UUID appointmentId) {




        Appointment appointment = appointmentRepositories.findByAppointmentId(appointmentId);
        if (appointment==null){
            throw new NullPointerException("Appointment not found with ID: " + appointmentId);
        }
       if (!"Pending".equals(appointment.getStatus())) {
           throw new IllegalStateException("Appointment must be in Pending status to Verified");
       }

        appointment.setStatus("Verified Coming");
        appointment.setUpdateAt(LocalDateTime.now());
        return appointmentRepositories.save(appointment);

    }

    @Transactional(rollbackFor = Exception.class)
    public Appointment recordReactionAndSetQualification(UUID appointmentId, String condition, boolean isQualified) {
        logger.debug("Recording reaction and setting qualification for appointmentId: {}, condition: {}, isQualified: {}",
                appointmentId, condition, isQualified);

        try {
            // Find the appointment
            Optional<Appointment> appointmentOpt = appointmentRepositories.findById(appointmentId);
            if (appointmentOpt.isEmpty()) {
                logger.error("Appointment not found for ID: {}", appointmentId);
                throw new NullPointerException("Appointment not found for ID: " + appointmentId);
            }

            Appointment appointment = appointmentOpt.get();
            List<Reaction> reactions = appointment.getReactions();
            if (reactions == null) {
                reactions = new ArrayList<>();
                appointment.setReactions(reactions);
            }

            // Add a new reaction
            Reaction reaction = new Reaction();
            reaction.setCondition(condition);
            reactions.add(reaction);

            // Set isOkay based on the qualification status
            if (isQualified) {
                appointment.setOkay(true); // isQualified == true, so isOkay = true
            } else {
                appointment.setOkay(false); // isQualified == false, so isOkay = false
            }

            // Save the appointment
            Appointment savedAppointment = appointmentRepositories.save(appointment);
            logger.info("Recorded reaction and set qualification for appointmentId: {}, isOkay: {}",
                    appointmentId, savedAppointment.isOkay());
            return savedAppointment;
        } catch (NullPointerException e) {
            logger.error("Failed to record reaction and set qualification for appointmentId: {}: {}",
                    appointmentId, e.getMessage(), e);
            throw e; // Re-throw to trigger transaction rollback
        } catch (Exception e) {
            logger.error("Unexpected error while recording reaction and setting qualification for appointmentId: {}: {}",
                    appointmentId, e.getMessage(), e);
            throw new RuntimeException("Failed to record reaction and set qualification for appointment " + appointmentId, e);
        }
    }

    @Transactional
    public Appointment cancelAppointment(UUID appointmentId) {

        Appointment appointment = appointmentRepositories.findById(appointmentId)
                .orElseThrow(() -> new NullPointerException("Appointment not found with ID: " + appointmentId));

        if (!"Pending".equals(appointment.getStatus())) {
            throw new IllegalStateException("Appointment must be in Pending status to cancel");
        }

        appointment.setStatus("Canceled");
        appointment.setUpdateAt(LocalDateTime.now());
        appointmentRepositories.save(appointment);


        return appointment;
    }

    @Transactional
    public Appointment markAppointmentAsCompleted(UUID appointmentId) {
        Appointment appointment = appointmentRepositories.findByAppointmentId(appointmentId);
        if (appointment == null) {
            throw new NullPointerException("Appointment not found with ID: " + appointmentId);
        }

        if (!"Verified Coming".equals(appointment.getStatus())) {
            throw new IllegalStateException("Appointment must be in 'Verified Coming' status to mark as completed");
        }


        appointment.setStatus("Completed");
        appointment.setUpdateAt(LocalDateTime.now());
        Appointment savedAppointment = appointmentRepositories.save(appointment);

        if (savedAppointment.isFinalDose()) {
            logger.info("Storing injection history for appointment {} as it is the final dose", appointmentId);
            Optional<Children> childrenOpt = childrenRepositories.findByChildrenId(savedAppointment.getChildrenId());
            if (!childrenOpt.isPresent()) {
                logger.error("Children record not found for appointment {}", appointmentId);
                throw new IllegalArgumentException("Children record not found for ID: " + savedAppointment.getChildrenId());
            }
            Children childrenn = childrenOpt.get();

            for (VaccineDetails vaccineDetail : savedAppointment.getVaccineDetailsList()) {
                Integer doseRequire = vaccineDetail.getDoseRequire();
                if (doseRequire == null) {
                    logger.warn("doseRequire is null for vaccineDetail with ID: {}. Skipping injection history for this vaccine.", vaccineDetail.getVaccineDetailsId());
                    continue;
                }
                InjectionHistory injectionHistory = new InjectionHistory();
                       injectionHistory.setId(UUID.randomUUID());
                        injectionHistory.setUserId(savedAppointment.getUserId());
                        injectionHistory.setChildrenId(savedAppointment.getChildrenId());
                        injectionHistory.setChildrenName(childrenn.getChildrenName());
                        injectionHistory.setVaccineDetailsId(vaccineDetail.getVaccineDetailsId());
                        injectionHistory.setDoseName(vaccineDetail.getDoseName());
                        injectionHistory.setDoseNumber(vaccineDetail.getDoseRequire());
                        injectionHistory.setInjectionDate(savedAppointment.getAppointmentDate());
                        injectionHistory.setAppointmentId(savedAppointment.getAppointmentId());
                        injectionHistoryRepositories.save(injectionHistory);
                        logger.info("Injection history saved for vaccineDetail {}", vaccineDetail.getVaccineDetailsId());
            }
        } else {
            logger.info("Not storing injection history for appointment {} as it is not the final dose", appointmentId);
        }

        try {
            createFollowUpAppointments(savedAppointment);
            notifyUserAboutNextAppointment(savedAppointment);
            return savedAppointment;
        } catch (Exception e) {
            logger.error("Error processing appointment {}: {}", appointmentId, e.getMessage(), e);
            throw new RuntimeException("Failed to process appointment completion for " + appointmentId, e);
        }
    }

        private void createFollowUpAppointments(Appointment originalAppointment) {
            List<VaccineDetails> vaccineDetailsList = originalAppointment.getVaccineDetailsList();

            for (VaccineDetails vaccine : vaccineDetailsList) {
                if (vaccine.getDoseRequire() != null && vaccine.getCurrentDose() != null
                        && vaccine.getCurrentDose() < vaccine.getDoseRequire()) {

                    int nextDose = vaccine.getCurrentDose() + 1;
                    UUID seriesId=vaccine.getVaccinationSeriesId();

                    if (appointmentRepositories.existsByUserIdAndVaccineDetailsListVaccineIdAndVaccineDetailsListCurrentDoseAndVaccineDetailsListVaccinationSeriesId(
                            originalAppointment.getUserId(), vaccine.getVaccineId(), nextDose, seriesId)) {
                        continue;
                    }
                    LocalDate nextAppointmentDate = originalAppointment.getAppointmentDate().plusDays(vaccine.getDateBetweenDoses());
                    Appointment followingAppointment = new Appointment();
                    followingAppointment.setAppointmentId(UUID.randomUUID());
                    followingAppointment.setUserId(originalAppointment.getUserId());
                    followingAppointment.setProcessId(originalAppointment.getProcessId());
                    followingAppointment.setChildrenId(originalAppointment.getChildrenId());
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
                    appointmentRepositories.save(followingAppointment);

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

        public boolean deleteByUserId(UUID userid){
        List<AppointmentDetail>appointmentDetailList=appointmentDetailsRepositories.findByUserId(userid);
        if (appointmentDetailList.isEmpty()){
            logger.warn("the list is null, nothing init");
        }
        appointmentDetailsRepositories.deleteAll(appointmentDetailList);
        return true;

    }

       @Transactional
       public AppointmentDetail updateStatusToPaid(UUID appointmentDetails){
   Optional<AppointmentDetail> appointmentDetail=appointmentDetailsRepositories.findByAppointmentDetailId(appointmentDetails);
           if(appointmentDetail==null){
               logger.warn("there is no appointment found with Id: " + appointmentDetails);
          }
           AppointmentDetail appdetails=appointmentDetail.get();
           Payment payment=paymentsRepositories.findByPaymentId(appdetails.getPaymentId());
           if (payment==null) {
               logger.error("Payment not found for ID: {}", appdetails.getPaymentId());
               throw new NullPointerException("Payment not found for ID: " + appdetails.getPaymentId());
           }
           Payment cashPayment=new Payment();
           cashPayment.setPaymentId(payment.getPaymentId());
           cashPayment.setUserId(payment.getUserId());
           cashPayment.setStatus("Success");
           cashPayment.setAppointmentId(payment.getAppointmentId());
           cashPayment.setAmount(payment.getAmount());
           cashPayment.setCreatedAt(LocalDateTime.now());
           paymentsRepositories.save(cashPayment);

           Optional<Appointment>appointment=appointmentRepositories.findById(appdetails.getAppointmentId());
           if(appointment.isEmpty()){
               throw new NullPointerException("there is no appointment found with ID");
           }
           Appointment appointmentt=appointment.get();
           appointmentt.setStatus("Pending");
           appointmentRepositories.save(appointmentt);

           appdetails.setPaymentStatus("Paid");
           appointmentDetailsRepositories.save(appdetails);
           return appdetails;

    }

    }



