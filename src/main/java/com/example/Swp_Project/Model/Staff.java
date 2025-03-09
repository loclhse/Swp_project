package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;


    @Document(collection = "Staff")
    @AllArgsConstructor
    @NoArgsConstructor
    public class Staff {
        @Id
        private UUID staff_id;  // Set to private
        private String name;    // Set to private
        private String phone;   // Set to private
        private String email;   // Set to private
        private String password;
        private String role;// Set to private
        private String status;   // Set to private
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate create_at; // Set to private

        // Getter and Setter for staff_id
        public UUID getStaff_id() {
            return staff_id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setStaff_id(UUID staff_id) {
            this.staff_id = staff_id;
        }

        // Getter and Setter for name
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        // Getter and Setter for phone
        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        // Getter and Setter for email
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        // Getter and Setter for password
        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        // Getter and Setter for status
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        // Getter and Setter for create_at
        public LocalDate getCreate_at() {
            return create_at;
        }

        public void setCreate_at(LocalDate create_at) {
            this.create_at = create_at;
        }

    }


