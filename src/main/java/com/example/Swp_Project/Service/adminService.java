package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Repositories.adminRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class adminService {
    @Autowired
    private adminRepositories adminRepositories;


    public List<Admin> getAllAdmins() {
        return adminRepositories.findAll();
    }

    public Optional<Admin> getAdminById(UUID adminId) {
        return adminRepositories.findById(adminId);
    }

    public Admin createAdmin(Admin admin) {
        admin.setAdmin_id(UUID.randomUUID());
        admin.setRole("Admin");
        admin.setStatus("Active");
        admin.setCreataAt(LocalDateTime.now());
        return adminRepositories.save(admin);
    }

    public Admin updateAdmin(UUID adminId, Admin updatedAdmin) {
        Optional<Admin> optionalAdmin = getAdminById(adminId);
        if (optionalAdmin.isPresent()) {
            Admin admin = optionalAdmin.get();
            admin.setEmail(updatedAdmin.getEmail());
            admin.setPassword(updatedAdmin.getPassword());
            admin.setStatus(updatedAdmin.getStatus());
            admin.setRole(updatedAdmin.getRole());
            admin.setCreataAt(updatedAdmin.getCreataAt());
            return adminRepositories.save(admin);
        } else {
            return null;
        }
    }

    public boolean deleteAdmin(UUID adminId) {
        Optional<Admin> optionalAdmin = getAdminById(adminId);
        if (optionalAdmin.isPresent()) {
            adminRepositories.deleteById(adminId);
            return true;
        } else {
            return false;
        }
    }
}
