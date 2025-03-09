package com.example.Swp_Project.CartDTO;

import org.springframework.data.annotation.Id;

import java.util.UUID;

public class CartItem {

    private UUID itemId;
    private String productName;
    private double price;
    private int quantity;

    public CartItem() {

    }
    public CartItem(UUID itemId, String productName, double price, int quantity) {
        this.itemId = itemId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    public UUID getItemId() { return itemId; }
    public void setItemId(UUID itemId) { this.itemId = itemId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
