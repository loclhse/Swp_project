package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


    @Document(collection = "Staff")
    @AllArgsConstructor
    @NoArgsConstructor
    public class Staff {
        @Id
        private UUID staffId;
        private String staffName;
        private String phone;
        private String email;
        private String password;
        private String role;
        private String status;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateAt;

        public String getRole() {
            return role;
        }
        public void setRole(String role) {
            this.role = role;
        }
        public UUID getStaffId() {return staffId;}
        public void setStaffId(UUID staffId) {this.staffId = staffId;}
        public String getStaffName() {return staffName;}
        public void setStaffName(String staffName) {this.staffName = staffName;}
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {this.email = email;}
        public String getPassword() {return password;}
        public void setPassword(String password) {this.password = password;}
        public String getStatus() {return status;}
        public void setStatus(String status) {this.status = status;}
        public LocalDateTime getCreateAt() {
            return createAt;
        }
        public void setCreateAt(LocalDateTime createAt) {
            this.createAt = createAt;
        }
        public LocalDateTime getUpdateAt() {
            return updateAt;
        }
        public void setUpdateAt(LocalDateTime updateAt) {
            this.updateAt = updateAt;
        }
    }


