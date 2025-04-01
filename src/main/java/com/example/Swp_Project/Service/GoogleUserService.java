package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.AuthResponseDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.UserRepositories;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleUserService {
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponseDTO processGoogleUser(OAuth2AuthenticationToken authentication) {
        // Extract user information from Google
        Map<String, Object> attributes = authentication.getPrincipal().getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Check if the user already exists in the database
        Optional<User> userOptional = userRepositories.findByEmail(email);
        User user;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        } else {
            // Create a new user if they don't exist
            user = new User();
            user.setEmail(email);
            user.setPassword("");
            user.setOtp(null);
            user.setRole("User");
            user.setOtpExpiration(0);
            user.setUserID(UUID.randomUUID());
            userRepositories.save(user);
        }


        String accessToken = jwtUtils.generateToken(name, email, user.getUserID(), user.getRole());

        return new AuthResponseDTO(email, accessToken, user.getRole(), user.getUserID());



    }

}
