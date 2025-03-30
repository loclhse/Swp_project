package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.UserDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.DTO.LoginRequestDTO;
import com.example.Swp_Project.Service.UserDetailsService;
import com.example.Swp_Project.Service.UserService;
import com.mongodb.internal.connection.OidcAuthenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.webjars.NotFoundException;

import java.net.URI;
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

    @GetMapping("/google-signIn-success")
    public ResponseEntity<?> googleSignInSuccess(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.error("Authentication failed: No principal found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Authentication failed"));
            }

            if (!(authentication.getPrincipal() instanceof OidcUser)) {
                logger.error("Authentication principal is not an OidcUser: {}", authentication.getPrincipal().getClass());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Invalid authentication principal"));
            }

            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : email;

            if (email == null) {
                logger.error("Google login failed: No email found in OidcUser");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Email not provided by Google"));
            }

            logger.info("Google login successful for email: {}", email);
            User user = usservice.saveOrUpdateGoogleUser(email, name);
            CustomUsersDetail userDetails = new CustomUsersDetail(user);

            String jwt = jwtUtils.generateToken(
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getUserID(),
                    userDetails.getRole()
            );


            String redirectUrl = "http://localhost:8080/auth/callback";
            URI redirectUri = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("token", jwt)
                    .queryParam("userID", userDetails.getUserID().toString())
                    .queryParam("username", userDetails.getUsername())
                    .queryParam("email", userDetails.getEmail())
                    .queryParam("role", userDetails.getRole())
                    .build()
                    .toUri();

            logger.info("Redirecting to frontend with JWT for userID: {}", userDetails.getUserID());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();

        } catch (RuntimeException e) {
            logger.error("Error during Google sign-in: {}", e.getMessage());
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
