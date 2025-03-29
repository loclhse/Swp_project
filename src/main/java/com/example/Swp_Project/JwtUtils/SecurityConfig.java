package com.example.Swp_Project.JwtUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Updated CSRF disable
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/",
                                 "/public/**",
                                 "/api/user/login",
                                 "/api/user/register",
                                 "/swagger-ui.html",
                                 "/swagger-ui/**",
                                 "/api-docs/**",
                                 "/api/vaccines-all",
                                "/api/vaccinedetails-all",
                                "/api/vaccines/{vaccineId}/details",
                                "/api/cart/return",
                                "/api/news-getall",
                                "/api/news/**").permitAll()
                        .requestMatchers("/api/**").authenticated().anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
