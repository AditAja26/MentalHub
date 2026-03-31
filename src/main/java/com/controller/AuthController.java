package com.controller;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.model.User;
import com.services.UserService;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "logout", required = false) String logout,
                                @RequestParam(value = "registered", required = false) String registered,
                                Model model) {
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        if (registered != null) {
            model.addAttribute("success", "Registration successful! Please login.");
        }
        return "authenticationModule/loginPage";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "authenticationModule/registerpage";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("email") String email,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        
        User user = userService.authenticate(email, password);

        if (user != null) {
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            
            // ALWAYS ask for mood on each login (not just once per day)
            // Reset mood flags on every new login session
            session.setAttribute("moodLoggedToday", false);
            session.setAttribute("moodSkippedToday", false);
            
            System.out.println(">>> New login session started for user: " + user.getName());
            System.out.println(">>> Mood tracking flags reset - will show quiz modal");
            
            String role = user.getRole().toLowerCase();
            if ("admin".equals(role)) {
                return "redirect:/admin";
            } else if ("advisor".equals(role)) {
                return "redirect:/advisor";
            } else {
                return "redirect:/student";
            }
        }

        model.addAttribute("error", "Invalid email or password.");
        return "authenticationModule/loginPage";
    }

    /**
     * Updated Register Logic: Now actually checks if passwords match!
     */
    @PostMapping("/register")
    public String processRegister(@RequestParam("name") String name,
                                  @RequestParam("email") String email,
                                  @RequestParam("password") String password,
                                  @RequestParam("confirmPassword") String confirmPassword, // ADDED THIS
                                  @RequestParam("role") String role,
                                  @RequestParam(value = "phone", required = false) String phone,
                                  @RequestParam(value = "age", required = false) Integer age,
                                  Model model) {
        
        // 1. THE FIX: Password Match Validation
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match! Please try again.");
            model.addAttribute("typedName", name);
            model.addAttribute("typedEmail", email);
            model.addAttribute("typedPhone", phone);
            model.addAttribute("typedAge", age);
            return "authenticationModule/registerpage";
        }

        // 2. Password Length Validation
        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters long.");
            model.addAttribute("typedName", name);
            model.addAttribute("typedEmail", email);
            model.addAttribute("typedPhone", phone);
            model.addAttribute("typedAge", age);
            return "authenticationModule/registerpage";
        }

        // 3. Email Duplicate Validation
        User existingUser = userService.getUserByEmail(email);
        if (existingUser != null) {
            model.addAttribute("error", "This email is already registered. Please use another or sign in.");
            model.addAttribute("typedName", name);
            model.addAttribute("typedPhone", phone);
            model.addAttribute("typedAge", age);
            return "authenticationModule/registerpage";
        }

        // 4. Everything is fine -> Save
        User newUser = new User();
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setRole(role.toLowerCase());
        newUser.setPhone(phone != null ? phone : "");
        newUser.setAge(age);
        
        userService.addUser(newUser);
        
        return "redirect:/login?registered=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); 
        return "redirect:/login?logout=true";
    }
}