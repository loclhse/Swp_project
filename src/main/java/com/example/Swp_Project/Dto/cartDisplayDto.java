package com.example.Swp_Project.Dto;

public class cartDisplayDto {
    private String doseName;
    private Double price;
    private Integer quantity;

    public cartDisplayDto(String doseName,Double price,Integer quantity) {
        this.doseName=doseName;
        this.price=price;
        this.quantity=quantity;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "SimpleCartItemDto{doseName='" + doseName + "', quantity=" + quantity + ", price=" + price + "}";
    }
}
