package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "VaccineDetails")
public class VaccineDetails {
    @Id
    private UUID vaccineDetailsId;
    private UUID vaccineId;
    private UUID vaccinationSeriesId;
    private Integer doseRequire;
    private Integer currentDose;
    private String doseName;
    private String imageUrl;
    private String manufacturer;
    private Integer quantity;
    private String status;
    private Integer dateBetweenDoses;
    private Double price;
    private Integer ageRequired;
    private Integer dosageAmount;
    private Integer boosterInterval;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;


    public Integer getBoosterInterval() {
        return boosterInterval;
    }

    public void setBoosterInterval(Integer boosterInteval) {
        this.boosterInterval = boosterInteval;
    }



    public Integer getDosageAmount() {
        return dosageAmount;
    }

    public void setDosageAmount(Integer dosageAmount) {
        this.dosageAmount = dosageAmount;
    }

    public Integer getAgeRequired() {
        return ageRequired;
    }

    public void setAgeRequired(Integer ageRequired) {
        this.ageRequired = ageRequired;
    }

    public Integer getCurrentDose() {
        return currentDose;
    }

    public void setCurrentDose(Integer currentDose) {
        this.currentDose = currentDose;
    }

    public UUID getVaccinationSeriesId() {
        return vaccinationSeriesId;
    }

    public void setVaccinationSeriesId(UUID vaccinationSeriesId) {
        this.vaccinationSeriesId = vaccinationSeriesId;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(UUID vaccineId) {
        this.vaccineId = vaccineId;
    }

    public UUID getVaccineDetailsId() {

        return vaccineDetailsId;
    }
    public void setVaccineDetailsId(UUID vaccineDetailsId) {
        this.vaccineDetailsId = vaccineDetailsId;
    }

    public Integer getDoseRequire() {
        return doseRequire;
    }

    public void setDoseRequire(Integer doseRequire) {
        this.doseRequire = doseRequire;
    }

    public String getDoseName() {
        return doseName;
    }

    public void setDoseName(String doseName) {
        this.doseName = doseName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getDateBetweenDoses() {
        return dateBetweenDoses;
    }

    public void setDateBetweenDoses(Integer dateBetweenDoses) {
        this.dateBetweenDoses = dateBetweenDoses;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
