package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.InjectionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface InjectionHistoryRepositories extends MongoRepository<InjectionHistory,UUID> {
}
