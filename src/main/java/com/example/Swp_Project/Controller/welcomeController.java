package com.example.Swp_Project.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class welcomeController {
    @GetMapping("/")
    public String welcome() {
        return "Welcome to Vaccine Management!";
    }
}
