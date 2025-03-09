package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface adminRepositories extends MongoRepository<Admin, UUID> {
}
