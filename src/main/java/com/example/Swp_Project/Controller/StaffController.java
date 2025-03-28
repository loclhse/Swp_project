package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.StaffDTO;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @GetMapping
    public List<Staff> getAllStaff() {
        return staffService.getAllStaff();
    }

    @GetMapping("/{staff_id}")
    public ResponseEntity<Staff> getStaffById(@PathVariable UUID staff_id) {
        return staffService.getStaffById(staff_id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Staff createStaff(@RequestBody StaffDTO staff) {
        return staffService.createStaff(staff);
    }

    @PutMapping("/{staff_id}")
    public ResponseEntity<Staff> updateStaff(@PathVariable UUID staff_id, @RequestBody StaffDTO staffDetails) {
        return ResponseEntity.ok(staffService.updateStaff(staff_id, staffDetails));
    }

    @DeleteMapping("/{staff_id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable UUID staff_id) {
        staffService.deleteStaff(staff_id);
        return ResponseEntity.noContent().build();
    }
}
