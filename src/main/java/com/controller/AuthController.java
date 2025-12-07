package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "authenticationModule/loginPage";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        return "authenticationModule/registerpage";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password) {
        // For now, redirect based on simple logic
        // In a real app, you'd validate credentials against database
        if (email.contains("admin")) {
            return "redirect:/admin";
        }
        return "redirect:/student";
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("role") String role) {
        // In a real app, you'd save user to database
        // For now, redirect to login
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }
}

