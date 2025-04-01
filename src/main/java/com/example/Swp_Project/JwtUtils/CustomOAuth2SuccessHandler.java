package com.example.Swp_Project.JwtUtils;

import com.example.Swp_Project.DTO.AuthResponseDTO;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Repositories.UserRepositories;
import com.example.Swp_Project.Service.GoogleUserService;
import com.example.Swp_Project.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler  {
    @Autowired
    private GoogleUserService googleUserService;
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private JwtUtils jwtUtils;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String SECRET_KEY = "6jSBqNjDF+HlVUMA5nOguNrWRqckFYfAPPgt3CpDOCo=";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String email = oidcUser.getEmail();
        String fullName = oidcUser.getFullName();


        User user = userRepositories.findByEmail(email)
                .orElseGet(() -> {
                    // Create new user
                    User newUser = new User();
                    newUser.setUserID(UUID.randomUUID());
                    newUser.setUsername(fullName);
                    newUser.setRole("User");
                    newUser.setEmail(email);
                    return userRepositories.save(newUser);
                });


        String token = jwtUtils.generateToken(user.getUsername(), user.getEmail(), user.getUserID(), user.getRole());
        String frontendRedirectUrl = "http://localhost:3000/auth/callback"
                + "?userId=" + URLEncoder.encode(user.getUserID().toString(), StandardCharsets.UTF_8)
                + "&userName=" + URLEncoder.encode(user.getUsername(), StandardCharsets.UTF_8)
                + "&userRole=" + URLEncoder.encode(user.getRole(), StandardCharsets.UTF_8)
                + "&token=" + URLEncoder.encode(token, StandardCharsets.UTF_8);


        response.sendRedirect(frontendRedirectUrl);



    }
}
