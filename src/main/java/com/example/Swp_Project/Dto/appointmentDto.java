package com.example.Swp_Project.Dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public class appointmentDto {
    private String childrenName;
    private String parentName;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String gender;
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate appointmentDate;
    @JsonFormat(pattern="HH:mm")
    private LocalTime timeStart;

    public String getChildrenName() {
        return childrenName;
    }

    public String getParentName() {
        return parentName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
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

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }
}
