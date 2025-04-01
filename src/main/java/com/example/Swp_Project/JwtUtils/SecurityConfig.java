package com.example.Swp_Project.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
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
                                "api/vaccinedetails-getById",
                                "/api/cart/return",
                                "/api/news-getall",
                                "/api/news/getById",
                                "/api/auth/**",
                                "/api/home",
                                "/favicon.ico",
                                "/api/auth/forgot-password",
                                "/api/auth/verify-otp",
                                "/api/auth/reset-password",
                        " /api/auth/user-info").permitAll()

                        .requestMatchers("/api/**").authenticated().anyRequest().authenticated()

                       )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
               .sessionManagement(session ->
                           session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));




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
