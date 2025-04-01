package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.AuthResponseDTO;
import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Service.GoogleUserService;
import com.example.Swp_Project.Service.UserDetailsService;
import com.example.Swp_Project.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;



import java.util.Arrays;


@RestController
public class GoogleSigninController {
    @Autowired
    private GoogleUserService googleUserService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/api/home")
    public ResponseEntity<?> home() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            System.out.println("AUTH TYPE: " + (authentication != null ? authentication.getClass().getName() : "null"));
            System.out.println("AUTH DETAILS: " + (authentication != null ? authentication.getDetails() : "null"));

            if (authentication == null || !(authentication instanceof OAuth2AuthenticationToken)) {
                System.out.println("Authentication is invalid in /api/home");
                return ResponseEntity.status(401).body("User not authenticated");
            }

            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            System.out.println("Processing OAuth token for: " + oauthToken.getPrincipal().getAttribute("email"));

            try {
                AuthResponseDTO authResponse = googleUserService.processGoogleUser(oauthToken);
                System.out.println("Auth response created successfully for: " + authResponse.getEmail());
                return ResponseEntity.ok(authResponse);
            } catch (Exception e) {
                System.err.println("Error in processGoogleUser: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(500).body("Error processing Google Sign-In: " + e.getMessage() + "\n\n" + Arrays.toString(e.getStackTrace()));
            }
        } catch (Exception e) {
            System.err.println("Unexpected error in /api/home: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Unexpected error: " + e.getMessage() + "\n\n" + Arrays.toString(e.getStackTrace()));
        }
    }


    @GetMapping("/api/auth/user-info")
    public OidcUser getUserInfo(Authentication authentication) {
        return (OidcUser) authentication.getPrincipal();
    }
}
