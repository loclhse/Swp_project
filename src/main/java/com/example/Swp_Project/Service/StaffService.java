package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.StaffDTO;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Repositories.StaffRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class StaffService {

    @Autowired
    private StaffRepositories staffRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public List<Staff> getAllStaff() {

        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(UUID staff_id) {

        return staffRepository.findById(staff_id);
    }

    public Staff createStaff(StaffDTO staff) {
        Staff staf=new Staff();
        staf.setStaffId(UUID.randomUUID());
        staf.setStaffName(staff.getStaffName());
        staf.setEmail(staff.getEmail());
        staf.setPassword(passwordEncoder.encode(staff.getPassword()));
        staf.setPhone(staff.getPhone());
        staf.setRole("Staff");
        staf.setStatus("Active");
        staf.setCreateAt(LocalDateTime.now());
        return staffRepository.save(staf);
    }
    public Staff updateStaff(UUID staff_id, StaffDTO staffDetails) {
        Staff staff = staffRepository.findById(staff_id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staff.setStaffName(staffDetails.getStaffName());
        staff.setPhone(staffDetails.getPhone());
        staff.setEmail(staffDetails.getEmail());
        staff.setPassword(passwordEncoder.encode(staffDetails.getPassword()));
        staff.setUpdateAt(LocalDateTime.now());
        return staffRepository.save(staff);
    }
    public void deleteStaff(UUID staff_id) {
        Staff staff = staffRepository.findById(staff_id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staffRepository.delete(staff);
    }
}
