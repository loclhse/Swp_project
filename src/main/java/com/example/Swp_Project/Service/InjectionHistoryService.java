package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.InjectionHistory;
import com.example.Swp_Project.Repositories.InjectionHistoryRepositories;
import jakarta.validation.constraints.Null;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
public class InjectionHistoryService {
    @Autowired
    private InjectionHistoryRepositories injectionHistoryRepositories;
    private static final Logger logger= LoggerFactory.getLogger(InjectionHistory.class);
    public List<InjectionHistory> findByChildrenIdDesc(UUID childrenId) throws NullPointerException {
        if (childrenId == null) {
            logger.error("Children ID must not be null");
            throw new IllegalArgumentException("Children ID must not be null");
        }

        List<InjectionHistory> histories = injectionHistoryRepositories.findByChildrenIdOrderByInjectionDateDesc(childrenId);
        if (histories.isEmpty()) {
            logger.error("No injection history found for childrenId: {}", childrenId);
            throw new NullPointerException("No injection history found for childrenId: " + childrenId);
        }

        logger.info("Found {} injection history records for childrenId: {}", histories.size(), childrenId);
        return histories;
    }

    public List<InjectionHistory> findByVaccineDetailsId(UUID vaccineDetailsId) throws NullPointerException{
        if (vaccineDetailsId == null) {
            logger.error("Vaccine Details ID must not be null");
            throw new IllegalArgumentException("Vaccine Details ID must not be null");
        }

        List<InjectionHistory> histories = injectionHistoryRepositories.findByVaccineDetailsId(vaccineDetailsId);
        if (histories.isEmpty()) {
            logger.error("No injection history found for vaccineDetailsId: {}", vaccineDetailsId);
            throw new NullPointerException("No injection history found for vaccineDetailsId: " + vaccineDetailsId);
        }

        logger.info("Found {} injection history records for vaccineDetailsId: {}", histories.size(), vaccineDetailsId);
        return histories;
    }

    public void deleteByChildrenId(UUID childrenId) throws NullPointerException {
        if (childrenId == null) {
            logger.error("Children ID must not be null");
            throw new IllegalArgumentException("Children ID must not be null");
        }

        List<InjectionHistory> histories = injectionHistoryRepositories.findByChildrenIdOrderByInjectionDateDesc(childrenId);
        if (histories.isEmpty()) {
            logger.error("No injection history found to delete for childrenId: {}", childrenId);
            throw new NullPointerException("No injection history found to delete for childrenId: " + childrenId);
        }

        injectionHistoryRepositories.deleteByChildrenId(childrenId);
        logger.info("Deleted injection history records for childrenId: {}", childrenId);
    }

    public List<InjectionHistory> getAll() {
        List<InjectionHistory> histories = injectionHistoryRepositories.findAll();
        if (histories.isEmpty()) {
            logger.error("No injection history records found");
            throw new NullPointerException("No injection history records found");
        }

        logger.info("Found {} injection history records", histories.size());
        return histories;
    }

    public List<InjectionHistory> findByUserId(UUID userId) {
        if (userId == null) {
            logger.error("User ID must not be null");
            throw new IllegalArgumentException("User ID must not be null");
        }

        List<InjectionHistory> histories = injectionHistoryRepositories.findByUserIdOrderByInjectionDateDesc(userId);
        if (histories.isEmpty()) {
            logger.error("No injection history found for userId: {}", userId);
            throw new NullPointerException("No injection history found for userId: " + userId);
        }

        logger.info("Found {} injection history records for userId: {}", histories.size(), userId);
        return histories;
    }


}
