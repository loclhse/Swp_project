package com.example.Swp_Project.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class appointmentDto {
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

    public String getChildrenName() {
        return childrenName;
    }

    public String getNote() {
        return note;
    }

    public String getMedicalIssue() {
        return medicalIssue;
    }

    public String getChildrenGender() {
        return childrenGender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }


    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setMedicalIssue(String medicalIssue) {
        this.medicalIssue = medicalIssue;
    }

    public void setChildrenGender(String childrenGender) {
        this.childrenGender = childrenGender;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

}
