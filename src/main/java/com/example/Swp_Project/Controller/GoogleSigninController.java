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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@RestController
public class GoogleSigninController {
    @Autowired
    private GoogleUserService googleUserService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/api/home")

    public ResponseEntity<?> home() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication instanceof OAuth2AuthenticationToken)) {
            System.out.println("Authentication is null or not an OAuth2AuthenticationToken in /api/home; redirecting to /api/auth/login");
            return ResponseEntity.status(401).body("User not authenticated");
        }

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        try {
            AuthResponseDTO authResponse = googleUserService.processGoogleUser(oauthToken);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing Google Sign-In: " + e.getMessage());
        }
    }


    @GetMapping("/api/auth/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Login failed. Please try again.");
        }
        return "Please sign in with Google: <a href=\"/oauth2/authorization/google\">Sign in with Google</a>"; // Thymeleaf template for login page
    }
}
