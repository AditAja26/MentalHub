package com.controller;

import com.model.User;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

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
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        // Validate credentials against database
        List<User> allUsers = userService.getAllUsers();
        User authenticatedUser = null;
        
        for (User user : allUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                authenticatedUser = user;
                break;
            }
        }
        
        if (authenticatedUser != null) {
            // Store user in session
            session.setAttribute("loggedInUser", authenticatedUser);
            session.setAttribute("userId", authenticatedUser.getId());
            session.setAttribute("userName", authenticatedUser.getName());
            session.setAttribute("userRole", authenticatedUser.getRole());
            
            // Redirect based on role
            String role = authenticatedUser.getRole().toLowerCase();
            if (role.equals("admin")) {
                return "redirect:/admin";
            } else if (role.equals("advisor")) {
                return "redirect:/advisor";
            } else {
                return "redirect:/student";
            }
        } else {
            // Login failed
            model.addAttribute("error", "Invalid email or password");
            return "authenticationModule/loginPage";
        }
    }

    @PostMapping("/register")
    public String processRegister(@RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("role") String role,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "age", required = false) Integer age,
                                  Model model) {
        // Check if email already exists
        List<User> allUsers = userService.getAllUsers();
        for (User existingUser : allUsers) {
            if (existingUser.getEmail().equals(email)) {
                model.addAttribute("error", "Email already registered. Please login or use a different email.");
                return "authenticationModule/registerpage";
            }
        }
        
        // Create new user
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role.toLowerCase()); // Normalize role to lowercase
        newUser.setPhone(phone != null ? phone : "");
        newUser.setAge(age);
        
        // Save to database
        userService.addUser(newUser);
        
        // Redirect to login with success message
        model.addAttribute("success", "Registration successful! Please login.");
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/notification")
    public String showNotificationPage(Model model) {
        return "notificationModule/NotificationPage";
    }
}

