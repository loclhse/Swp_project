package com.example.Swp_Project.Model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
@Document(collection = "Payments")
public class CashPayment extends Payment{
    private LocalDateTime paydate;

    public LocalDateTime getPaydate() {
        return paydate;
    }

    public void setPaydate(LocalDateTime paydate) {
        this.paydate = paydate;
    }
}
