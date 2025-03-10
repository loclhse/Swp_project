package com.example.Swp_Project.Model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

public class customUsersDetail implements UserDetails {
        private String username;
        private String email;
        private UUID userID;
        private String password;
        private String status;
        private String role;

    public customUsersDetail(UUID userID,String username,String role,String email) {
        this.userID = userID;
        this.username=username;
        this.role=role;
        this.email=email;
    }

    public customUsersDetail(User user) {
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.userID = user.getUserID();
            this.password = user.getPassword();
            this.status = user.getStatus();
            this.role=user.getRole();
        }
    public customUsersDetail(Staff staff) {
        this.username = staff.getStaffName(); // Using name as username
        this.email = staff.getEmail();
        this.userID = staff.getStaffId();
        this.password = staff.getPassword();
        this.status = staff.getStatus();
        this.role = staff.getRole();
    }
    public customUsersDetail(Admin admin) {
        this.username = admin.getAdminName(); // Using email as username since no name field
        this.email = admin.getEmail();
        this.userID = admin.getAdminId();
        this.password = admin.getPassword();
        this.status = admin.getStatus();
        this.role = admin.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    public UUID getUserID() { return userID; }
    public String getEmail() { return email; }
    public String getStatus() { return status; }
    public String getRole() { return role; }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return "active".equals(status); }
}









