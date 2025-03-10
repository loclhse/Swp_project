package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Document (collection = "Admin")
public class Admin {
    @Id
private UUID adminId;
    private String adminName;
    private String email;
    private String password;
    private String status;
    private String role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creataAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    public String getAdminName() {return adminName;}

    public void setAdminName(String adminName) {this.adminName = adminName;}

    public LocalDateTime getUpdateAt() {return updateAt;}

    public void setUpdateAt(LocalDateTime updateAt) {this.updateAt = updateAt;}

    public LocalDateTime getCreataAt() {return creataAt;}

    public void setCreataAt(LocalDateTime creataAt) {this.creataAt = creataAt;}

    public UUID getAdminId() {return adminId;}

    public void setAdminId(UUID adminId) {this.adminId = adminId;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getStatus() {return status;}

    public void setStatus(String status) {this.status = status;}

    public String getRole() {return role;}

    public void setRole(String role) {this.role = role;}
}
