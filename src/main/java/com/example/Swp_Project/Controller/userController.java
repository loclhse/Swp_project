package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.userRegisterDTO;
import com.example.Swp_Project.Model.customUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Dto.LoginRequest;
import com.example.Swp_Project.Service.userDetailsService;
import com.example.Swp_Project.Service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequestMapping("/api/user")
@RestController
public class userController {
    @Autowired
    private userService usservice;
    @Autowired
    private userDetailsService usdetail;
    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        try {

            String token = usservice.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            UserDetails userDetails = usdetail.loadUserByUsername(loginRequest.getEmail());
            customUsersDetail customUserDetails = (customUsersDetail) userDetails;

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userID", customUserDetails.getUserID());
            response.put("username", customUserDetails.getUsername());
            response.put("email", customUserDetails.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody userRegisterDTO us) {
        try {
            User createdUser = usservice.register(us);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
        @ExceptionHandler(Exception.class)
        public ResponseEntity<?> handleException (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + ex.getMessage());
        }
        @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = usservice.getAllUsers();
        return ResponseEntity.ok(users); // 200
    }
    @GetMapping("/{username}")
    public ResponseEntity<User> findByUsername(@RequestParam String username) {
        Optional<User> user = usservice.findByUsername(username);
        return user.map(ResponseEntity::ok) // 200
                .orElseGet(() -> ResponseEntity.notFound().build()); // 404
    }
    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody userRegisterDTO user) {
       try {
             User updatedUser = usservice.updateUser(userId, user);
             return ResponseEntity.ok(updatedUser); // 200
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        try {
            usservice.deleteUser(userId);
            return ResponseEntity.noContent().build(); // 204
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    }
