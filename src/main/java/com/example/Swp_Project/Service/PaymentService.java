package com.example.Swp_Project.Service;
import com.example.Swp_Project.DTO.DailyRevenueDTO;
import com.example.Swp_Project.Model.Payment;
import com.example.Swp_Project.Repositories.PaymentsRepositories;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;



import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentsRepositories paymentsRepositories;

    public List<Payment> getAllPayments() {
        return paymentsRepositories.findAll();
    }

    public List<Payment> getPaymentsByUserId(UUID userId) {

        List<Payment>paymentList=paymentsRepositories.findByUserId(userId);
        if(paymentList.isEmpty()){
            throw new NullPointerException("there is no vaccine found for ID: " + userId);
        }
        return paymentList;
    }

    public boolean deletePaymentByUserId(UUID userId){
        List<Payment>paymentList=paymentsRepositories.findByUserId(userId);
        if(paymentList.isEmpty()){
            throw new NullPointerException("there is no vaccine found for ID: " + userId);
        }
        paymentsRepositories.deleteAll(paymentList);
        return true;
    }


    public List<DailyRevenueDTO> calculateRevenueByDays(boolean includePending) {

        List<Payment> payments = paymentsRepositories.findAll();

        List<Payment> filteredPayments = includePending
                ? payments
                : payments.stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()) || "Success".equalsIgnoreCase(p.getStatus()))
                .collect(Collectors.toList());

        Map<String, List<Payment>> paymentsByDays = filteredPayments.stream()
                .collect(Collectors.groupingBy(
                        payment -> payment.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ));

        List<DailyRevenueDTO> dailyRevenues = paymentsByDays.entrySet().stream()
                .map(entry -> {
                    String day = entry.getKey();
                    List<Payment> dailyPayments = entry.getValue();
                    double totalRevenue = dailyPayments.stream()
                            .mapToLong(Payment::getAmount)
                            .sum();
                    int paymentCount = dailyPayments.size();
                    return new DailyRevenueDTO(day, totalRevenue, paymentCount);
                })
                .sorted(Comparator.comparing(DailyRevenueDTO::getDay))
                .collect(Collectors.toList());

        return dailyRevenues;
    }




}
