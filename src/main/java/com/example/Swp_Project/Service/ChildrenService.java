package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.ChildrenDTO;
import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Repositories.ChildrenRepositories;
import com.example.Swp_Project.Repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChildrenService {
    @Autowired
    private ChildrenRepositories childrenRepository;
    @Autowired
    private UserRepositories userRepositories;


    public List<Children> getAllChildren() {
        return childrenRepository.findAll();
    }

    public Children childrenCreate(UUID userId, ChildrenDTO childrenDTO) {
        Children child = new Children();
        child.setChildrenName(childrenDTO.getChildrenName());
        child.setDateOfBirth(childrenDTO.getDateOfBirth());
        child.setGender(childrenDTO.getGender());
        child.setMedicalIssue(childrenDTO.getMedicalIssue());
        child.setChildrenId(UUID.randomUUID());
        child.setUserId(userId);
        child.setCreatAt(LocalDateTime.now());
        return childrenRepository.save(child);
    }

    public Optional<Children> updateChild(UUID childrenId, ChildrenDTO childrenDTO) {
        Optional<Children> existingChild = childrenRepository.findById(childrenId);
        if (existingChild.isPresent()) {
            Children child = existingChild.get();
            child.setChildrenName(childrenDTO.getChildrenName());
            child.setDateOfBirth(childrenDTO.getDateOfBirth());
            child.setGender(childrenDTO.getGender());
            child.setMedicalIssue(childrenDTO.getMedicalIssue());
            child.setUpdateAt(LocalDateTime.now()); // Update timestamp
            return Optional.of(childrenRepository.save(child));
        }
        return Optional.empty();
    }

    public List<Children> getAllChildrenByUserId(UUID userId) {
        return childrenRepository.findByUserId(userId);
    }

    public Optional<Children> getChildById(UUID childrenId) {
        return childrenRepository.findByChildrenId(childrenId);
    }

    public void deleteChild(UUID childrenId) {
        if (!childrenRepository.existsById(childrenId)) {
            throw new NullPointerException("Child with ID " + childrenId + " not found");
        }
        childrenRepository.deleteById(childrenId);
    }


}

