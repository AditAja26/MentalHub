package com.controller;

import com.model.Goal;
import com.model.User;
import com.services.GoalService;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/student")
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private GoalService goalService;

    // Hardcoded for testing, though usually handled by session
    private static final Long CURRENT_USER_ID = 1L; 

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        
        // If user doesn't exist, create a default one
        if (user == null) {
            user = new User(null, "Hakimi", 22, "hakimi@email.com", "082337729130", "password", "student");
            userService.addUser(user);
        }
        
        model.addAttribute("user", user);
        return "studentModule/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        model.addAttribute("user", user);
        return "studentModule/editProfile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("name") String name,
                                @RequestParam("email") String email,
                                @RequestParam(value = "password", required = false) String password,
                                @RequestParam("phone") String phone) {
        User user = userService.getUserById(CURRENT_USER_ID);
        if (user != null) {
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);
            if (password != null && !password.isEmpty()) {
                user.setPassword(password);
            }
            userService.updateUser(CURRENT_USER_ID, user);
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/goals")
    public String showGoals(Model model) {
        List<Goal> activeGoals = goalService.getActiveGoalsByUserId(CURRENT_USER_ID);
        List<Goal> completedGoals = goalService.getCompletedGoalsByUserId(CURRENT_USER_ID);
        
        model.addAttribute("activeGoals", activeGoals);
        model.addAttribute("completedGoals", completedGoals);
        return "studentModule/goals";
    }

    @PostMapping("/goals/add")
    public String addGoal(@RequestParam("goalDescription") String description) {
        // FIX: We must fetch the User object first
        User currentUser = userService.getUserById(CURRENT_USER_ID);
        
        if (currentUser != null) {
            // FIX: Pass the 'currentUser' object, not just the ID
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