package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.Children;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ChildrenRepositories extends MongoRepository<Children, UUID> {
List<Children>findByUserId(UUID userId);
}
