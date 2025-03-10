package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.adminDto;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.adminRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public List<Admin> getAllAdmins() {

        return adminRepositories.findAll();
    }
    public Optional<Admin> getAdminById(UUID adminId) {

        return adminRepositories.findById(adminId);
    }
    public Admin createAdmin(adminDto admin) {
        Admin ad=new Admin();
        ad.setAdminId(UUID.randomUUID());
        ad.setAdminName(admin.getAdminName());
        ad.setEmail(admin.getEmail());
        ad.setPassword(passwordEncoder.encode(admin.getPassword()));
        ad.setRole("Admin");
        ad.setStatus("Active");
        ad.setCreataAt(LocalDateTime.now());
        return adminRepositories.save(ad);
    }
    public Admin updateAdmin(UUID adminId, adminDto updatedAdmin) {
        Optional <Admin> Admin = getAdminById(adminId);
        if (Admin.isPresent()) {
            Admin admin = Admin.get();
            admin.setEmail(updatedAdmin.getEmail());
            admin.setPassword(passwordEncoder.encode(updatedAdmin.getPassword()));
            admin.setUpdateAt(LocalDateTime.now());
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
