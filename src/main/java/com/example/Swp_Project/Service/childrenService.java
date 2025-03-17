package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.childrenDto;
import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.childrenRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.webjars.NotFoundException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class childrenService {
    @Autowired
    private childrenRepositories childrenRepository;
    @Autowired
    private userRepositories userRepositories;


    public List<Children> getAllChildren() {
        return childrenRepository.findAll();
    }

    public Children childrenCreate(UUID userId, childrenDto childrenDTO) {
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

    public Optional<Children> updateChild(UUID childrenId, childrenDto childrenDTO) {
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
        return childrenRepository.findById(childrenId);
    }

    public void deleteChild(UUID childrenId) {
        if (!childrenRepository.existsById(childrenId)) {
            throw new NotFoundException("Child with ID " + childrenId + " not found");
        }
        childrenRepository.deleteById(childrenId);
    }

}

