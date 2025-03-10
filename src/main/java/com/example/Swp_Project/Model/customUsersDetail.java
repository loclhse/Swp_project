package com.example.Swp_Project.Model;

import org.springframework.security.core.GrantedAuthority;
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

        public customUsersDetail(User user) {
            this.username = user.getUsername();
            this.email = user.getEmail();
            this.userID = user.getUserID();
            this.password = user.getPassword();
            this.status = user.getStatus();
        }

    public customUsersDetail(String username,String email,UUID userID) {
        this.username = username;
        this.email=email;
        this.userID=userID;
    }

    @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            // Return authorities (roles) if needed
            return Collections.emptyList();
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        public UUID getUserID() {
            return userID;
        }

        public String getEmail() {
            return email;
        }

        public String getStatus() {
            return status;
        }




    }



