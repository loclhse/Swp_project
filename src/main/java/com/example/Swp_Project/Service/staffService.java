package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Repositories.staffRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class staffService {
    @Autowired
    private staffRepositories  staffRepository;

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(UUID staff_id) {
        return staffRepository.findById(staff_id);
    }

    public Staff createStaff(Staff staff) {
        staff.setStaff_id(UUID.randomUUID());
        staff.setRole("Staff");
        staff.setStatus("Active");
        staff.setCreate_at(LocalDate.now());
        return staffRepository.save(staff);
    }

    public Staff updateStaff(UUID staff_id, Staff staffDetails) {
        Staff staff = staffRepository.findById(staff_id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staff.setName(staffDetails.getName());
        staff.setPhone(staffDetails.getPhone());
        staff.setEmail(staffDetails.getEmail());
        staff.setPassword(staffDetails.getPassword());
        staff.setRole(staffDetails.getRole());
        staff.setStatus(staffDetails.getStatus());
        return staffRepository.save(staff);
    }

    public void deleteStaff(UUID staff_id) {
        Staff staff = staffRepository.findById(staff_id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staffRepository.delete(staff);
    }
}
