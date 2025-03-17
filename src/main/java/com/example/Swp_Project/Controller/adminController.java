package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.adminDto;
import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Service.adminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class adminController {
    @Autowired
    private adminService adminService;

    @GetMapping("/admin-all")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }

    @GetMapping("/admin-get/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable UUID id) {
        Optional<Admin> admin = adminService.getAdminById(id);
        return admin.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping("/admin-create")
    public ResponseEntity<Admin> createAdmin(@RequestBody adminDto admin) {
        Admin createdAdmin = adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
    }

    @PutMapping("/admin-update/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable UUID id, @RequestBody adminDto updatedAdmin) {
        Admin admin = adminService.updateAdmin(id, updatedAdmin);
        if (admin == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(admin);
    }

    @DeleteMapping("/admin-delete/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        boolean deleted = adminService.deleteAdmin(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.noContent().build();
    }
}

