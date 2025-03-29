package com.example.Swp_Project.DTO;

public class DailyRevenueDTO {
    private String day;
    private double totalRevenue;
    private int paymentCount;

    public DailyRevenueDTO(String day, double totalRevenue, int paymentCount) {
        this.day = day;
        this.totalRevenue = totalRevenue;
        this.paymentCount = paymentCount;
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public int getPaymentCount() { return paymentCount; }
    public void setPaymentCount(int paymentCount) { this.paymentCount = paymentCount; }
}

