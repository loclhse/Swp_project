package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.AdminRepositories;
import com.example.Swp_Project.Repositories.StaffRepositories;
import com.example.Swp_Project.Repositories.UserRepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
@Autowired
    private UserRepositories userRepository;
@Autowired
    private StaffRepositories staffRepo;
@Autowired
    private AdminRepositories adminRepo;
@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user != null) {
        return new CustomUsersDetail(user);
    }
    Staff staff = staffRepo.findByEmail(email).orElse(null);
    if (staff != null) {
        return new CustomUsersDetail(staff);
    }
    Admin admin = adminRepo.findByEmail(email).orElse(null);
    if (admin != null) {
        return new CustomUsersDetail(admin);
    }

    throw new UsernameNotFoundException("User not found with email: " + email);
   }
}
