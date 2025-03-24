package com.example.Swp_Project.Model;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class CartItem {
    private UUID vaccineDetailsId;
    private int quantity;

    public CartItem() {
    }

    public UUID getVaccineDetailsId() {
        return vaccineDetailsId;
    }

    public void setVaccineDetailsId(UUID vaccineDetailsId) {
        this.vaccineDetailsId = vaccineDetailsId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
