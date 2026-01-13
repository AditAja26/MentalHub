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