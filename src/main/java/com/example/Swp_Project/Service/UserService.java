package com.example.Swp_Project.Service;


import com.example.Swp_Project.DTO.RequestResetPasswordDTO;
import com.example.Swp_Project.DTO.UserDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.AdminRepositories;
import com.example.Swp_Project.Repositories.StaffRepositories;
import com.example.Swp_Project.Repositories.UserRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private UserRepositories usrepo;
    @Autowired
    private StaffRepositories staffRepo;
    @Autowired
    private AdminRepositories adminRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private OtpService otpService;

    public User register(UserDTO user) {
        if (user.getUsername() == null || user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("Username, email, and password are required fields.");
        }
        if (usrepo.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists.");
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }
        User us=new User();
        us.setUserID(UUID.randomUUID());
        us.setUsername(user.getUsername());
        us.setEmail(user.getEmail());
        us.setPassword(passwordEncoder.encode(user.getPassword()));
        us.setAddress(user.getAddress());
        us.setPhone(user.getPhone());
        us.setDateOfBirth(user.getDateOfBirth());
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

    public User updateUser(UUID userId, UserDTO updatedUser) {
       Optional<User> existingUserOpt = usrepo.findById(userId);
       if (existingUserOpt.isEmpty()) {
           throw new RuntimeException("User not found with ID: " + userId);
       }
       User existingUser = existingUserOpt.get();
       existingUser.setUsername(updatedUser.getUsername());
       existingUser.setEmail(updatedUser.getEmail());
       existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
       existingUser.setAddress(updatedUser.getAddress());
       existingUser.setPhone(updatedUser.getPhone());
       existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
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

    public User findByUserId(UUID id){
        return usrepo.findByUserID(id);
    }

    public User updateUserProfile(UUID userId, UserDTO updatedUser) {
        User existingUser = usrepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));


        if (updatedUser.getUsername() != null) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        if (updatedUser.getEmail() != null) {
            if (usrepo.findByEmail(updatedUser.getEmail()).isPresent() &&
                    !updatedUser.getEmail().equals(existingUser.getEmail())) {
                throw new RuntimeException("Email already exists.");
            }
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPassword() != null && updatedUser.getConfirmPassword() != null) {
            if (!updatedUser.getPassword().equals(updatedUser.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getPhone() != null) {
            existingUser.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updatedUser.getDateOfBirth());
        }

        existingUser.setUpdateAt(LocalDateTime.now());
        return usrepo.save(existingUser);
    }

    @Transactional
    public User deactivateUser(UUID userID) {
        User user = usrepo.findByUserID(userID);
        if (user == null) {
            throw new NotFoundException("User not found with ID: " + userID);
        }
        if ("Deactivated".equals(user.getStatus())) {
            throw new IllegalStateException("User is already deactivated");
        }
        user.setStatus("Deactivated");
        User updatedUser = usrepo.save(user);
        return updatedUser;
    }

    public User saveOrUpdateGoogleUser(String email, String name) {
        Optional<User> userOpt = usrepo.findByEmail(email);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            user.setUsername(name);
        } else {
            user = new User();
            user.setUserID(UUID.randomUUID());
            user.setEmail(email);
            user.setUsername(name);
            user.setRole("User");
            user.setStatus("Active");
        }
        return usrepo.save(user);
    }

    public String initiateForgotPassword(String email) {
        Optional<User> userOptional = usrepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found with the provided email.";
        }

        User user = userOptional.get();
        String otp = otpService.generateOtp();
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plusSeconds(600).toEpochMilli());
        usrepo.save(user);

        // Send OTP via email
        otpService.sendOtpEmail(email, otp);

        return "OTP sent to your email.";
    }

    public String verifyOtp(String email, String otp) {
        Optional<User> userOptional = usrepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found.";
        }

        User user = userOptional.get();
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return "Invalid OTP.";
        }

        if (Instant.now().toEpochMilli() > user.getOtpExpiration()) {
            return "OTP has expired.";
        }

        return "OTP verified successfully.";
    }

    public String resetPassword(String email, RequestResetPasswordDTO newPassword) {
        Optional<User> userOptional = usrepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            return "User not found.";
        }
        if(!newPassword.getNewPassword().equals(newPassword.getConfirmNewpassword())){
            return "not match, pls do again";
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(newPassword.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiration(0);
        usrepo.save(user);

        return "Password reset successfully.";
    }

}