package com.example.Swp_Project.Repositories;

import com.example.Swp_Project.Model.InjectionHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface InjectionHistoryRepositories extends MongoRepository<InjectionHistory,UUID> {
    List<InjectionHistory> findByChildrenIdOrderByInjectionDateDesc(UUID childrenId);
    List<InjectionHistory> findByVaccineDetailsId(UUID vaccineDetailsId);
    void deleteByChildrenId(UUID childrenId);
    List<InjectionHistory> findByUserIdOrderByInjectionDateDesc(UUID userId);
}
