package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.DailyRevenueDTO;
import com.example.Swp_Project.Model.Payment;
import com.example.Swp_Project.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;
    @GetMapping("/payments-all")
    public ResponseEntity<List<Payment>> getAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/payments-getByUserId/{userId}")
    public ResponseEntity<List<Payment>> getPaymentsByUserId(@PathVariable UUID userId) {
        try {
            List<Payment> payments = paymentService.getPaymentsByUserId(userId);
            return ResponseEntity.ok(payments);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/payments/{userId}/user")

    public ResponseEntity<Void> deletePaymentByUserId(@PathVariable UUID userId) {
        try {
            paymentService.deletePaymentByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("payments/revenue/daily")

    public ResponseEntity<List<DailyRevenueDTO>> calculateRevenueByMonth(
            @RequestParam(defaultValue = "false") boolean includePending) {
        try {
            List<DailyRevenueDTO> monthlyRevenues = paymentService.calculateRevenueByDays(includePending);
            return ResponseEntity.ok(monthlyRevenues);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
