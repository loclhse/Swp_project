package com.example.Swp_Project.Model;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class OtpStorage {

    private final Map<String, OtpData> otpMap = new HashMap<>();

    public void storeOtp(String email, String otp, LocalDateTime expirationTime) {
        otpMap.put(email, new OtpData(otp, expirationTime));
    }

    public OtpData getOtpData(String email) {
        return otpMap.get(email);
    }

    public void removeOtp(String email) {
        otpMap.remove(email);
    }

    public static class OtpData {
        private final String otp;
        private final LocalDateTime expirationTime;

        public OtpData(String otp, LocalDateTime expirationTime) {
            this.otp = otp;
            this.expirationTime = expirationTime;
        }

        public String getOtp() {
            return otp;
        }

        public LocalDateTime getExpirationTime() {
            return expirationTime;
        }
    }
}