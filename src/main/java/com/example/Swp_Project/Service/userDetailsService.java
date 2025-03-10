package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Admin;
import com.example.Swp_Project.Model.Staff;
import com.example.Swp_Project.Model.customUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.adminRepositories;
import com.example.Swp_Project.Repositories.staffRepositories;
import com.example.Swp_Project.Repositories.userRepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailsService implements UserDetailsService {
@Autowired
    private userRepositories userRepository;
@Autowired
    private staffRepositories staffRepo;
@Autowired
    private adminRepositories adminRepo;
@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElse(null);
    if (user != null) {
        return new customUsersDetail(user);
    }
    Staff staff = staffRepo.findByEmail(email).orElse(null);
    if (staff != null) {
        return new customUsersDetail(staff);
    }
    Admin admin = adminRepo.findByEmail(email).orElse(null);
    if (admin != null) {
        return new customUsersDetail(admin);
    }

    throw new UsernameNotFoundException("User not found with email: " + email);
   }
}
