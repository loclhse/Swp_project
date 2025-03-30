package com.example.Swp_Project.Controller;

import com.example.Swp_Project.JwtUtils.JwtUtils;
import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Service.UserDetailsService;
import com.example.Swp_Project.Service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Collections;

@RestController
@RequestMapping("/api")
public class GoogleSignIn {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserService usservice;
    @Autowired
    private UserDetailsService usdetail;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/auth/google-signIn-success")
    public ResponseEntity<?> googleSignInSuccess(Authentication authentication) {
        try {
            if (authentication == null || authentication.getPrincipal() == null) {
                logger.error("Authentication failed: No principal found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Collections.singletonMap("error", "Authentication failed"));
            }

            if (!(authentication.getPrincipal() instanceof OidcUser)) {
                logger.error("Authentication principal is not an OidcUser: {}", authentication.getPrincipal().getClass());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Collections.singletonMap("error", "Invalid authentication principal"));
            }

            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName() != null ? oidcUser.getFullName() : email;

            if (email == null) {
                logger.error("Google login failed: No email found in OidcUser");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Email not provided by Google"));
            }

            logger.info("Google login successful for email: {}", email);
            User user = usservice.saveOrUpdateGoogleUser(email, name);
            CustomUsersDetail userDetails = new CustomUsersDetail(user);

            String jwt = jwtUtils.generateToken(
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getUserID(),
                    userDetails.getRole()
            );


            String redirectUrl = "http://localhost:8080/auth/callback";
            URI redirectUri = UriComponentsBuilder.fromUriString(redirectUrl)
                    .queryParam("token", jwt)
                    .queryParam("userID", userDetails.getUserID().toString())
                    .queryParam("username", userDetails.getUsername())
                    .queryParam("email", userDetails.getEmail())
                    .queryParam("role", userDetails.getRole())
                    .build()
                    .toUri();

            logger.info("Redirecting to frontend with JWT for userID: {}", userDetails.getUserID());
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(redirectUri)
                    .build();

        } catch (RuntimeException e) {
            logger.error("Error during Google sign-in: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        }
    }
}
