package com.example.Swp_Project.JwtUtils;

import com.example.Swp_Project.DTO.AuthResponseDTO;
import com.example.Swp_Project.Service.GoogleUserService;
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

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private GoogleUserService googleUserService;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Fetch the AuthResponseDTO
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        AuthResponseDTO authResponse = googleUserService.processGoogleUser(oauthToken);

        // Store the AuthResponseDTO in an HTTP-only cookie (or session attribute)
        Cookie authCookie = new Cookie("authResponse", authResponse.toString());
        authCookie.setHttpOnly(true);
        authCookie.setPath("/");
        authCookie.setSecure(request.getScheme().equals("https")); // Secure in production (HTTPS)
        response.addCookie(authCookie);

        // Redirect to the frontend
        response.sendRedirect("http://localhost:3000//auth/google-callback");
    }
}
