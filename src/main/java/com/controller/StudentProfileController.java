package com.controller;

import com.model.Goal;
import com.model.User;
import com.services.GoalService;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Redirect non-students to their appropriate dashboard
        if (userRole != null && !userRole.equalsIgnoreCase("student")) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            }
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "studentModule/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "studentModule/editProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam("phone") String phone,
                                HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Create a partial user object with only the fields to update
        User updatedUser = new User();
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);
        if (password != null && !password.isEmpty()) {
            updatedUser.setPassword(password);
        }
        
        // Update the user - this will fetch the existing user internally
        User user = userService.updateUser(userId, updatedUser);
        
        if (user != null) {
            // Update session attributes to reflect the changes
            session.setAttribute("userName", name);
            session.setAttribute("loggedInUser", user);
        }
        return "redirect:/student";
    }

    @GetMapping("/goals")
    public String showGoals(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Redirect non-students to their appropriate dashboard
        if (userRole != null && !userRole.equalsIgnoreCase("student")) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            }
        }
        
        List<Goal> activeGoals = goalService.getActiveGoalsByUserId(userId);
        List<Goal> completedGoals = goalService.getCompletedGoalsByUserId(userId);
        
        model.addAttribute("activeGoals", activeGoals);
        model.addAttribute("completedGoals", completedGoals);
        return "studentModule/goals";
    }

    @PostMapping("/goals/add")
    public String addGoal(@RequestParam("goalDescription") String description, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        User currentUser = userService.getUserById(userId);
        if (currentUser != null) {
            Goal goal = new Goal(currentUser, description, false);
            goalService.addGoal(goal);
        }
        
        return "redirect:/student/goals";
    }

    @GetMapping("/goals/{id}/complete")
    public String completeGoal(@PathVariable Long id) {
        goalService.completeGoal(id);
        return "redirect:/student/goals";
    }

    @GetMapping("/goals/{id}/delete")
    public String deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return "redirect:/student/goals";
    }
}

