package com.example.Swp_Project.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Document(collection = "Payments")
public class Payment {
    @Id
    private UUID paymentId;
    private UUID userId;
    private UUID appointmentId;
    private UUID vaccineDetailsID;
    private Long amount;
    private String paymentMethod;
    private String status;
    private List<VaccineDetails>vaccineDetailsList;
    private LocalDateTime createdAt;

    public UUID getVaccineDetailsID() {
        return vaccineDetailsID;
    }

    public void setVaccineDetailsID(UUID vaccineDetailsID) {
        this.vaccineDetailsID = vaccineDetailsID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<VaccineDetails> getVaccineDetailsList() {
        return vaccineDetailsList;
    }

    public void setVaccineDetailsList(List<VaccineDetails> vaccineDetailsList) {
        this.vaccineDetailsList = vaccineDetailsList;
    }

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}