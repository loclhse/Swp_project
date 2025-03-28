package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jdk.jshell.Snippet;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Document(collection = "AppointmentDetails")
public class AppointmentDetail {
        @Id
        private UUID appointmentDetailId;
        private UUID appointmentId;
        private UUID paymentId;
        private String childrenName;
        @JsonFormat(pattern="yyyy-MM-dd")
        private LocalDate appointmentDate;
        @JsonFormat(pattern="HH:mm")
        private LocalTime timeStart;
        private UUID userId;
        private String paymentMethod;
        private String paymentStatus;
        private LocalDateTime createAt;

    public UUID getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(UUID paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalTime getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(LocalTime timeStart) {
        this.timeStart = timeStart;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public UUID getAppointmentDetailId() {
        return appointmentDetailId;
    }

    public String getChildrenName() {
        return childrenName;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setAppointmentDetailId(UUID appointmentDetailId) {
        this.appointmentDetailId = appointmentDetailId;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }
}
