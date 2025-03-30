package com.example.Swp_Project.DTO;

public class LoginResponseDTO {
    private String jwt;
    private String role;
    private String userID;

    public LoginResponseDTO(String jwt, String role, String userID) {
        this.jwt = jwt;
        this.role = role;
        this.userID = userID;
    }

    public String getJwt() { return jwt; }
    public void setJwt(String jwt) { this.jwt = jwt; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }
}
