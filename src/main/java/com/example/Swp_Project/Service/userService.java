package com.example.Swp_Project.Service;


import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.Children;
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

    public User register(User user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required fields.");
        }

        // Check if email already exists
        if (usrepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }

        // Initialize user details
        user.setUserID(UUID.randomUUID());
        user.setRole("User");
        user.setStatus("Active");
        user.setCreatedAt(LocalDateTime.now());

        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            return usrepo.save(user);
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
    public User addChildToUser(UUID userId, Children children) {
        Optional<User> userOpt = usrepo.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User user = userOpt.get();
        children.setChildrenId(UUID.randomUUID());
        children.setCreatAt(LocalDateTime.now());
        List<Children>childrenList = user.getChildrens();
        if (childrenList == null) {
            childrenList = new ArrayList<>();
        }
        childrenList.add(children); // Add the whole Children entity
        user.setChildrens(childrenList);

        return usrepo.save(user);
    }

    public List<User> getAllUsers() {
        return usrepo.findAll();
    }
    public User updateUser(UUID userId, User updatedUser) {
        Optional<User> existingUserOpt = usrepo.findById(userId);
        if (existingUserOpt.isEmpty()) {
            throw new RuntimeException("User not found with ID: " + userId);
        }

        User existingUser = existingUserOpt.get();
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setPassword(updatedUser.getPassword());
        existingUser.setRole(updatedUser.getRole());
        existingUser.setStatus(updatedUser.getStatus());
        existingUser.setChildrens(updatedUser.getChildrens()); // Update children list

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