package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.RequestResetPasswordDTO;
import com.example.Swp_Project.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ResetPasswordController {
    @Autowired
    private UserService userService;

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<String> initiateForgotPassword(@RequestParam String email) {
        try {
            String result = userService.initiateForgotPassword(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error initiating forgot password: " + e.getMessage());
        }
    }


    @PostMapping("/auth/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        try {
            String result = userService.verifyOtp(email, otp);
            if (result.equals("OTP verified successfully.")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error verifying OTP: " + e.getMessage());
        }
    }


    @PostMapping("/auth/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestParam String email,
            @RequestBody RequestResetPasswordDTO newPassword) {
        try {
            String result = userService.resetPassword(email, newPassword);
            if (result.equals("Password reset successfully.")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error resetting password: " + e.getMessage());
        }
    }
}
