
package com.example.Swp_Project.JwtUtils;

import com.example.Swp_Project.DtoUltils.customUsersDetail;
import com.example.Swp_Project.Service.userDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private userDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {


        String token = getJwtFromRequest(request);


        if (token != null && jwtUtil.validateToken(token)) {
            try {

                String username = jwtUtil.getUsernameFromToken(token);
                String email = jwtUtil.getEmailFromToken(token);
                UUID userID = jwtUtil.getUserIDFromToken(token);


                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 2.3: Create an authentication object with authorities
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Step 2.4: Store user-specific details in the Principal (optional)
                // Optionally, you could store additional user info into Principal or as a custom field
                customUsersDetail userDetailsWithInfo = new customUsersDetail(username, email, userID);
                authentication.setDetails(userDetailsWithInfo); // Store user details (username, email, userID)

                // Step 2.5: Set the authentication in SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {

                System.out.println("Error processing token: " + e.getMessage());
            }
        } else {

            System.out.println("Invalid or missing token.");
        }

        // Continue with the request-response chain
        chain.doFilter(request, response);
    }




    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check if the Authorization header is present and starts with "Bearer "
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();  // Extract token and trim any extra spaces
        }

        // Optionally, log the issue if the token is not found or invalid
        System.out.println("Authorization header is missing or does not start with 'Bearer '.");

        return null; // Return null if the token is not present or incorrectly formatted
    }
}

