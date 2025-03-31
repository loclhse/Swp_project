package com.example.Swp_Project.DTO;

import java.util.UUID;

public class AuthResponseDTO {
    private String email;
    private String accessToken;
    private String role;
    private UUID userID;

    public AuthResponseDTO(String email, String accessToken,String role, UUID userID) {
        this.email = email;
        this.accessToken = accessToken;
        this.role = role;
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRole() {
        return role;
    }

    public UUID getUserID() {
        return userID;
    }
}
