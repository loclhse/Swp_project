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


    public Children addChildToUser(UUID userId, Children children) {
        Optional<User> userOpt = userRepositories.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        User user = userOpt.get();
            children.setChildrenId(UUID.randomUUID());
            children.setCreatAt(LocalDateTime.now());
            children.setUserId(userId);
        userRepositories.save(user);
        List<Children>childrenList = user.getChildrens();
            childrenList.add(children); // Add the whole Children entity
            user.setChildrens(childrenList);
return childrenRepository.save(children);

    }

    public ResponseEntity<User> deleteUserChild(UUID userId, UUID childId) {
        Optional<User> userOpt = userRepositories.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        User user = userOpt.get();
        List<Children> childrenList = user.getChildrens();
        if (childrenList == null || childrenList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        boolean removed = childrenList.removeIf(child -> child.getChildrenId().equals(childId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Child not found in User
        }
        user.setChildrens(childrenList);
        childrenRepository.deleteById(childId);
        return ResponseEntity.ok(user);
    }

    public List<Children> getChildrenByUserId(UUID userId) {
         User user = userRepositories.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
         List<Children> childrenList = user.getChildrens();
        if (childrenList == null || childrenList.isEmpty()) {
            throw new RuntimeException("There are no children. You can create your child's info on the homepage.");
        }
        return childrenList;
    }
    public Children getChildrenById(UUID childrenId){
        return childrenRepository.findById(childrenId).orElseThrow(()->new RuntimeException("there is not children with that ID"));
    }
    public Children updateChild(UUID userId, UUID childId, Children updatedChild) {

        User user = userRepositories.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Children> childrenList = Optional.ofNullable(user.getChildrens())
                .filter(list -> !list.isEmpty())
                .orElseThrow(() -> new RuntimeException("No children found for this user."));

        Children childToUpdate = childrenList.stream()
                .filter(child -> child.getChildrenId().equals(childId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Child not found with ID: " + childId));
        if (updatedChild.getChildrenName() != null) childToUpdate.setChildrenName(updatedChild.getChildrenName());
        if (updatedChild.getDateOfBirth() != null) childToUpdate.setDateOfBirth(updatedChild.getDateOfBirth());
        if (updatedChild.getParentName() != null) childToUpdate.setParentName(updatedChild.getParentName());
        if (updatedChild.getGender() != null) childToUpdate.setGender(updatedChild.getGender());
        if (updatedChild.getAge() != null) childToUpdate.setAge(updatedChild.getAge());
        if (updatedChild.getMedicalHistory() != null) childToUpdate.setMedicalHistory(updatedChild.getMedicalHistory());
        childToUpdate.setCreatAt(LocalDateTime.now());

        return childrenRepository.save(childToUpdate);
    }

}
