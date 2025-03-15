package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;


@Document(collection = "Appointment")
public class Appointment {
    @Id
    private UUID appointmentId;
    private UUID userId;
    private String childrenName;
    private String note;
    private String medicalIssue;
    private String childrenGender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate appointmentDate;
    @JsonFormat(pattern="HH:mm")
    private LocalTime timeStart;
    private List<Feedback>feedbacks=new ArrayList<>();
    private List<VaccineDetails>vaccineDetailsList;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    public Appointment() {
    }

    public Appointment(UUID userId, String childrenName, String parentName, LocalDate appointmentDate, LocalTime timeStart) {
        this.appointmentId = UUID.randomUUID();
        this.userId = userId;
        this.childrenName = childrenName;
        this.appointmentDate = appointmentDate;
        this.timeStart = timeStart;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public String getChildrenName() {
        return childrenName;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMedicalIssue() {
        return medicalIssue;
    }

    public void setMedicalIssue(String medicalIssue) {
        this.medicalIssue = medicalIssue;
    }

    public String getChildrenGender() {
        return childrenGender;
    }

    public void setChildrenGender(String childrenGender) {
        this.childrenGender = childrenGender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<VaccineDetails> getVaccineDetailsList() {
        return vaccineDetailsList;
    }

    public void setVaccineDetailsList(List<VaccineDetails> vaccineDetailsList) {
        this.vaccineDetailsList = vaccineDetailsList;
    }
}
