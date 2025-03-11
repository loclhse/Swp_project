package com.example.Swp_Project.Dto;

public class vaccineDetailsDto {
    private Integer doseRequire;
    private String doseName;
    private String imageUrl;
    private String manufacturer;
    private Integer quantity;
    private String status;
    private Integer dateBetweenDoses;
    private Double price;
    public Integer getDoseRequire() {
        return doseRequire;
    }

    public String getDoseName() {
        return doseName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getStatus() {
        return status;
    }

    public Integer getDateBetweenDoses() {
        return dateBetweenDoses;
    }

    public Double getPrice() {
        return price;
    }

    // Setters
    public void setDoseRequire(Integer doseRequire) {
        this.doseRequire = doseRequire;
    }

    public void setDoseName(String doseName) {
        this.doseName = doseName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateBetweenDoses(Integer dateBetweenDoses) {
        this.dateBetweenDoses = dateBetweenDoses;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
