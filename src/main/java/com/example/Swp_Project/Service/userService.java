package com.example.Swp_Project.Service;


import com.example.Swp_Project.Dto.userDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.adminRepositories;
import com.example.Swp_Project.Repositories.childrenRepositories;
import com.example.Swp_Project.Repositories.staffRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private staffRepositories staffRepo;
    @Autowired
    private adminRepositories adminRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(userDTO user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required fields.");
        }
        if (usrepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }
        User us=new User();
        us.setUserID(UUID.randomUUID());
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

        User user = usrepo.findByEmail(email).orElse(null);
        if (user != null) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid password");}

            return jwtUtil.generateToken(
                    user.getUsername(),
                    user.getEmail(),
                    user.getUserID(),
                    user.getRole()
            );}

        Staff staff = staffRepo.findByEmail(email).orElse(null);
        if (staff != null) {
            if (!passwordEncoder.matches(password, staff.getPassword())) {
                throw new RuntimeException("Invalid password");}

            return jwtUtil.generateToken(
                    staff.getStaffName(),
                    staff.getEmail(),
                    staff.getStaffId(),
                    staff.getRole()
            );}

        Admin admin = adminRepo.findByEmail(email).orElse(null);
        if (admin != null) {
            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new RuntimeException("Invalid password");
            }
            return jwtUtil.generateToken(
                    admin.getEmail(),
                    admin.getEmail(),
                    admin.getAdminId(),
                    admin.getRole()
            );
        }

        throw new RuntimeException("User not found");
    }
    public List<User> getAllUsers() {
        return usrepo.findAll();
    }
   public User updateUser(UUID userId, userDTO updatedUser) {
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