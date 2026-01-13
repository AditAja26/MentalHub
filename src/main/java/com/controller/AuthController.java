package com.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.model.User;
import com.services.UserService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "logout", required = false) String logout,
                                Model model) {
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        return "authenticationModule/loginPage";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Prepare an empty user object for the registration form
        model.addAttribute("user", new User());
        return "authenticationModule/registerpage";
    }

    /**
     * Authenticates the user and starts a session.
     */
    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        
        User user = userService.authenticate(email, password);

        if (user != null) {
            // IMPORTANT: Store the user in the session for the whole team to use
            session.setAttribute("loggedInUser", user);
            
            // Redirect based on the role stored in the database
            String role = user.getRole().toLowerCase();
            if ("admin".equals(role)) {
                return "redirect:/admin";
            } else if ("advisor".equals(role)) {
                return "redirect:/advisor";
            } else {
                return "redirect:/student";
            }
        }

        // If authentication fails
        model.addAttribute("error", "Invalid email or password.");
        return "authenticationModule/loginPage";
    }

    /**
     * Registers a new user including Age and Phone from the form.
     */
    @PostMapping("/register")
    public String processRegister(@ModelAttribute("user") User user, 
                                 @RequestParam("confirmPassword") String confirmPassword, 
                                 Model model) {
        
        // 1. Check if passwords match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            return "authenticationModule/registerpage";
        }

        // 2. Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("student");
        }

        // 3. Save the user (UserService handles the Hibernate save)
        User registeredUser = userService.registerUser(user);
        
        if (registeredUser != null) {
            return "redirect:/login?registered=true";
        } else {
            model.addAttribute("error", "Registration failed. This email is already registered.");
            return "authenticationModule/registerpage";
        }
    }

    /**
     * Clears the session and logs the user out.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login?logout=true";
    }
    
    @GetMapping("/notification")
    public String showNotificationPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login"; 
        }
        return "notificationModule/NotificationPage";
    }
}