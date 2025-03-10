package com.example.Swp_Project.Service;


import com.example.Swp_Project.Dto.userRegisterDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.childrenRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class userService {
@Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private userRepositories usrepo;
    @Autowired
    private childrenRepositories childrenRepositories;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User register(userRegisterDTO user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required fields.");
        }
        if (usrepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }
        User us=new User();
        us.setUsername(user.getUsername());
        us.setEmail(user.getEmail());
        us.setPassword(passwordEncoder.encode(user.getPassword()));
        us.setRole("User");
        us.setStatus("Active");
        us.setCreatedAt(LocalDateTime.now());
        us.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return usrepo.save(us);
        } catch (DataAccessException ex) {
            throw new RuntimeException("Database error: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error: " + ex.getMessage(), ex);
        }
    }
    public String authenticateUser(String email, String password) {
        User user = usrepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return jwtUtil.generateToken(user.getUsername(),user.getEmail(),user.getUserID());  // Use email for token generation
    }
    public List<User> getAllUsers() {
        return usrepo.findAll();
    }
   public User updateUser(UUID userId, userRegisterDTO updatedUser) {
       Optional<User> existingUserOpt = usrepo.findById(userId);
       if (existingUserOpt.isEmpty()) {
           throw new RuntimeException("User not found with ID: " + userId);
       }
       User existingUser = existingUserOpt.get();
       existingUser.setUsername(updatedUser.getUsername());
       existingUser.setEmail(updatedUser.getEmail());
       existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
       existingUser.setUpdateAt(LocalDateTime.now());
       return usrepo.save(existingUser);
   }
    public void deleteUser(UUID userId) {
        Optional<User> userOpt = usrepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        usrepo.deleteById(userId);
    }
    public Optional<User> findByUsername(String username) {
        return usrepo.findByUsername(username);
    }
}