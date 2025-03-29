package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentsRepositories extends MongoRepository<Payment, UUID> {
    Payment findByPaymentId(UUID paymentId);
    List<Payment>findByUserId(UUID userId);
}
