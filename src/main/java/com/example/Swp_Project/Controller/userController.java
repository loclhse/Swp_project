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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    }@ExceptionHandler(Exception.class)
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
    public ResponseEntity<User> updateUser(@PathVariable UUID userId, @RequestBody userDTO user) {
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
