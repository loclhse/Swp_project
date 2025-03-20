package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface paymentsRepositories extends MongoRepository<Payment, UUID> {
}
