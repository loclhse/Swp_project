package com.example.Swp_Project.Dto;

public class cartDisplayDto {
    private String doseName;
    private Double price;
    private String imgUrl;
    private Integer doseRequire;
    private String manufacturer;
    private Integer amount;


    public cartDisplayDto(String doseName,Integer doseRequire,String manufacturer,Double price,Integer amount,String imgUrl) {
        this.doseName=doseName;
        this.price=price;
        this.doseRequire=doseRequire;
        this.manufacturer=manufacturer;
        this.amount=amount;
        this.imgUrl=imgUrl;

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "cartDisplayDto{" +
                "doseName='" + doseName + '\'' +
                ", price=" + price +
                ", doseRequire=" + doseRequire +
                ", manufacturer='" + manufacturer + '\'' +
                ", amount=" + amount +
                '}';
    }
}
