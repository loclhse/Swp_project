package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.customUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.userRepositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailsService implements UserDetailsService {
@Autowired
    private userRepositories userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch the user from the database by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert the User entity to CustomUserDetails and return it
        return new customUsersDetail(user);
    }
}
