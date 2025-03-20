package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.userDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.customUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Dto.LoginRequest;
import com.example.Swp_Project.Service.userDetailsService;
import com.example.Swp_Project.Service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.*;

@RequestMapping("/api/user")
@RestController
public class userController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private userService usservice;
    @Autowired
    private userDetailsService usdetail;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequest loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email or password missing"));
            }
            String token = usservice.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            customUsersDetail userDetails = (customUsersDetail) usdetail.loadUserByUsername(loginRequest.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("userID", userDetails.getUserID());
            response.put("username", userDetails.getUsername());
            response.put("email", userDetails.getEmail());
            response.put("role", userDetails.getRole());

            String role = userDetails.getRole() != null ? userDetails.getRole() : "unknown";
            switch (role) {
                case "Admin":
                    response.put("message", "Welcome to Admin Dashboard");
                    break;
                case "Staff":
                    response.put("message", "Welcome to Staff Dashboard");
                    break;
                case "User":
                    response.put("message", "Welcome to User Dashboard");
                    break;
                default:
                    response.put("message", "Welcome to Default Dashboard");
            }

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody userDTO us) {
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
    public ResponseEntity<?> handleException(Exception ex) {
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

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID userId) {
        try {
            usservice.deleteUser(userId);
            return ResponseEntity.noContent().build(); // 204
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam("userId") UUID requestedUserId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            customUsersDetail userDetails = (customUsersDetail) auth.getDetails();
            UUID authenticatedUserId = userDetails.getUserID();

            if (!authenticatedUserId.equals(requestedUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "You can only view your own profile"));
            }

            User user = usservice.findByUserId(requestedUserId);
            Map<String, Object> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("address", user.getAddress());
            response.put("phone", user.getPhone());
            response.put("dateOfBirth", user.getDateOfBirth());


            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody userDTO updatedUser) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            customUsersDetail userDetails = (customUsersDetail) auth.getDetails();
            UUID userIdd = userDetails.getUserID();

            User updated = usservice.updateUserProfile(userIdd, updatedUser);
            return ResponseEntity.ok(Map.of(
                    "message", "Profile updated successfully"
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }

    @PutMapping("/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            customUsersDetail userDetails = (customUsersDetail) auth.getDetails();
            UUID userId = userDetails.getUserID();

            User deactivatedUser = usservice.deactivateUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User account deactivated successfully");
            response.put("user", deactivatedUser);
            return ResponseEntity.ok(response);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Something went wrong"));
        }
    }
}
