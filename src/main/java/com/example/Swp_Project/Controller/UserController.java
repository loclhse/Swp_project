package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.UserDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.DTO.LoginRequestDTO;
import com.example.Swp_Project.Service.UserDetailsService;
import com.example.Swp_Project.Service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.web.bind.annotation.*;



import java.util.*;

@RequestMapping("/api/user")
@RestController
public class UserController {

    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService usservice;
    @Autowired
    private UserDetailsService usdetail;

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody LoginRequestDTO loginRequest) {
        try {
            if (loginRequest.getEmail() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Email or password missing"));
            }
            String token = usservice.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            CustomUsersDetail userDetails = (CustomUsersDetail) usdetail.loadUserByUsername(loginRequest.getEmail());

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
    public ResponseEntity<?> createUser(@RequestBody UserDTO us) {
        try {
            User createdUser = usservice.register(us);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
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
            CustomUsersDetail userDetails = (CustomUsersDetail) auth.getDetails();
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
    public ResponseEntity<?> updateProfile(@RequestBody UserDTO updatedUser) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            CustomUsersDetail userDetails = (CustomUsersDetail) auth.getDetails();
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

    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable UUID userId) {
        try {
            User deactivatedUser = usservice.deactivateUser(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User account deactivated successfully");
            response.put("user", deactivatedUser);
            return ResponseEntity.ok(response);
        } catch (NullPointerException e) {
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

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        try {
            usservice.sendOtpForRegistration(email);
            return ResponseEntity.ok("OTP sent to " + email);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
