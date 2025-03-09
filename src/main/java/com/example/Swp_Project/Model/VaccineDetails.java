package com.example.Swp_Project.Model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "VaccineDetails")
public class VaccineDetails {
    @Id
    private UUID vaccineDetailsId = UUID.randomUUID();
    private UUID vaccineId;
    private Integer doseRequire;
    private String doseName;
    private String manufacturer;
    private Integer quantity;
    private String status;
    private Integer dateBetweenDoses;
    private Double price;

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
}
