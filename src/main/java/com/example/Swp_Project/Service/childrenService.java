package com.example.Swp_Project.Service;

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

    public Children getChildrenById(UUID childrenId) {
        return childrenRepository.findById(childrenId).orElse(null);
    }

    public Children createChildren(Children children) {
        return childrenRepository.save(children);
    }

    public Children updateChildren(UUID childrenId, Children childrenDetails) {
        Children children = childrenRepository.findById(childrenId).orElse(null);
        if (children != null) {
            children.setChildrenName(childrenDetails.getChildrenName());
            children.setDateOfBirth(childrenDetails.getDateOfBirth());
            children.setGender(childrenDetails.getGender());
            children.setMedicalIssue(childrenDetails.getMedicalIssue());
            children.setUserId(childrenDetails.getUserId());
            return childrenRepository.save(children);
        }
        return null;
    }

    public void deleteChildren(UUID childrenId) {
        childrenRepository.deleteById(childrenId);
    }
    public List<Children> getChildrenByUserId(UUID userId) {
        return childrenRepository.findByUserId(userId);
    }
}
