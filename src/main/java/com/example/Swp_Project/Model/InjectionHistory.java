package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.UUID;

public class InjectionHistory {
    @Id
    private UUID id;
    private String childrenName;
    private UUID childrenId;
    private UUID vaccineDetailsId;
    private String doseName;
    private Integer doseNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime injectionDate;
    private UUID appointmentId;

    public InjectionHistory() {
    }

    public InjectionHistory(UUID childrenId,String childrenName, UUID vaccineDetailsId,String doseName, Integer doseNumber, LocalDateTime injectionDate, UUID appointmentId) {
        this.childrenId = childrenId;
        this.childrenName=childrenName;
        this.vaccineDetailsId = vaccineDetailsId;
        this.doseName=doseName;
        this.doseNumber = doseNumber;
        this.injectionDate = injectionDate;
        this.appointmentId = appointmentId;

    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(Integer doseNumber) {
        this.doseNumber = doseNumber;
    }

    public LocalDateTime getInjectionDate() {
        return injectionDate;
    }

    public void setInjectionDate(LocalDateTime injectionDate) {
        this.injectionDate = injectionDate;
    }

    public UUID getVaccineDetailsId() {
        return vaccineDetailsId;
    }

    public void setVaccineDetailsId(UUID vaccineDetailsId) {
        this.vaccineDetailsId = vaccineDetailsId;
    }

    public UUID getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(UUID childrenId) {
        this.childrenId = childrenId;
    }
}
