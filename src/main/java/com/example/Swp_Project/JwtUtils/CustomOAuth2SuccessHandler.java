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
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private GoogleUserService googleUserService;
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private JwtUtils jwtUtils;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        AuthResponseDTO authResponse = googleUserService.processGoogleUser(oauthToken);


        ObjectMapper objectMapper = new ObjectMapper();
        String authResponseJson = objectMapper.writeValueAsString(authResponse);

        // Đặt cookie như trước
        Cookie authCookie = new Cookie("authResponse", authResponseJson);
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setSecure(request.getScheme().equals("https"));
        response.addCookie(authCookie);

        // Thêm tham số vào URL callback để frontend có thể sử dụng
        String encodedAuthResponse = URLEncoder.encode(authResponseJson, StandardCharsets.UTF_8.toString());

        // Truyền thông tin trong URL để frontend có thể truy cập
        response.sendRedirect("http://localhost:3000/auth/google-callback?auth=" + encodedAuthResponse);
    }

    public AuthResponseDTO processGoogleUser(OAuth2AuthenticationToken token) {
        try {
            // Thêm log để theo dõi xử lý
            System.out.println("Processing Google user: " + token.getPrincipal().getAttribute("email"));

            // Extract user information from Google
            Map<String, Object> attributes = token.getPrincipal().getAttributes();
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
                user.setPassword(""); // No password needed for Google users
                user.setOtp(null);
                user.setOtpExpiration(0);
                user.setUserID(UUID.randomUUID());
                user.setRole("User"); // Set default role to "User"
                userRepositories.save(user);
            }

            // Generate access token and refresh token
            String accessToken = jwtUtils.generateToken(name, email, user.getUserID(), user.getRole());


            // Return AuthResponseDTO
            return new AuthResponseDTO(email, accessToken, user.getRole(), user.getUserID());
        } catch (Exception e) {
            System.err.println("Error in processGoogleUser: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to process Google user: " + e.getMessage(), e);
        }
    }
}
