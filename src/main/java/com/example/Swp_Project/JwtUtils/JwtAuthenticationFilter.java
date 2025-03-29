
package com.example.Swp_Project.JwtUtils;

import com.example.Swp_Project.Model.CustomUsersDetail;
import com.example.Swp_Project.Service.UserDetailsService;
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
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
              String token = getJwtFromRequest(request);
              if(token == null || !jwtUtil.validateToken(token)) {
              response.setStatus(HttpServletResponse.SC_FORBIDDEN);
              response.getWriter().write("{\"error\": \"Invalid or missing token.\"}");
              response.setContentType("application/json");
              response.setCharacterEncoding("UTF-8");
              return;

              }

              try {
                  String role=jwtUtil.getRoleFromToken(token);
                  String username = jwtUtil.getUsernameFromToken(token);
                  String email = jwtUtil.getEmailFromToken(token);
                  UUID userID = jwtUtil.getUserIDFromToken(token);
                  UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                CustomUsersDetail userDetailsWithInfo = new CustomUsersDetail(userID,username,email,role);
                authentication.setDetails(userDetailsWithInfo);
                SecurityContextHolder.getContext().setAuthentication(authentication);

              } catch (Exception e) {
                  System.out.println("Error processing token: " + e.getMessage());
                  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                  response.getWriter().write("{\"error\": \"Error processing authentication.\"}");
                  response.setContentType("application/json");
                  response.setCharacterEncoding("UTF-8");
                  return;
              }
              chain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        System.out.println("Authorization header is missing or does not start with 'Bearer '.");
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/") || path.startsWith("/public/")
                || path.equals("/api/user/login")
                || path.equals("/api/user/register")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/api/vaccines-all")
                || path.startsWith("/api/vaccinedetails-all")
                || path.startsWith("/vaccines/{vaccineId}/details")
                || path.startsWith("/api/news/getById")
                || path.equals("/api/cart/return") || (path.startsWith("/api/cart/return"))
                || path.startsWith("/api/news-getall");
    }
}

