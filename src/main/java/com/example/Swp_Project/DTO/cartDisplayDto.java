package com.example.Swp_Project.DTO;

import com.example.Swp_Project.Model.CartItem;
import com.example.Swp_Project.Model.VaccineDetails;

import java.util.UUID;

public class cartDisplayDto {
    private UUID vaccineDetailsId;
    private int quantity;
    private Integer doseRequire;
    private String doseName;
    private String manufacturer;
    private Integer dateBetweenDoses;
    private Double price;


    public cartDisplayDto(CartItem cartItem, VaccineDetails vaccineDetails) {
        this.vaccineDetailsId = cartItem.getVaccineDetailsId();
        this.quantity = cartItem.getQuantity();
        this.doseRequire = vaccineDetails.getDoseRequire();
        this.doseName = vaccineDetails.getDoseName();
        this.manufacturer = vaccineDetails.getManufacturer();
        this.dateBetweenDoses = vaccineDetails.getDateBetweenDoses();
        this.price = vaccineDetails.getPrice();
    }


    public UUID getVaccineDetailsId() { return vaccineDetailsId; }
    public void setVaccineDetailsId(UUID vaccineDetailsId) { this.vaccineDetailsId = vaccineDetailsId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public Integer getDoseRequire() { return doseRequire; }
    public void setDoseRequire(Integer doseRequire) { this.doseRequire = doseRequire; }
    public String getDoseName() { return doseName; }
    public void setDoseName(String doseName) { this.doseName = doseName; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Integer getDateBetweenDoses() { return dateBetweenDoses; }
    public void setDateBetweenDoses(Integer dateBetweenDoses) { this.dateBetweenDoses = dateBetweenDoses; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
}
