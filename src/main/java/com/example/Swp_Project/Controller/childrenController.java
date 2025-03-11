package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Children;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Service.childrenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/children")
public class childrenController {

    @Autowired
    private childrenService childrenService;

    @PostMapping("/{userId}")
    public ResponseEntity<Children> addChildToUser(
            @PathVariable UUID userId,
            @RequestBody Children children) {
        try {
            Children updatedUser = childrenService.addChildToUser(userId, children);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @DeleteMapping("{childId}/user/{userId}")
    public ResponseEntity<User> deleteUserChild(
            @PathVariable UUID userId,
            @PathVariable UUID childId) {
        return childrenService.deleteUserChild(userId, childId);
    }

    @GetMapping("/{userId}/user")
    public ResponseEntity<List<Children>> getUserChildren(@PathVariable UUID userId) {
        try {
            List<Children> children = childrenService.getChildrenByUserId(userId);
            return ResponseEntity.ok(children);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{userId}/children/{childId}")
       public ResponseEntity<Children> updateChild(@PathVariable UUID userId, @PathVariable UUID childId,
                                                   @RequestBody Children updatedChild) {
        try {
            Children updated = childrenService.updateChild(userId, childId, updatedChild);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
