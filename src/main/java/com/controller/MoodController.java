package com.controller;

import com.model.User;
import com.services.MoodLogService;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/mood")
public class MoodController {

    @Autowired
    private MoodLogService moodLogService;

    @Autowired
    private UserService userService;

    /**
     * Handles mood logging from the quiz modal
     * @param q1 Question 1: Overall feeling (1-5)
     * @param q2 Question 2: Sleep quality (1-5)
     * @param q3 Question 3: Stress level (1-5)
     * @param q4 Question 4: Focus ability (1-5)
     * @param q5 Question 5: Social connection (1-5)
     * @param session HTTP session containing user info
     * @return Redirect to appropriate landing page
     */
    @PostMapping("/log")
    public String logMood(@RequestParam(value = "q1", required = false) Integer q1,
                          @RequestParam(value = "q2", required = false) Integer q2,
                          @RequestParam(value = "q3", required = false) Integer q3,
                          @RequestParam(value = "q4", required = false) Integer q4,
                          @RequestParam(value = "q5", required = false) Integer q5,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        
        System.out.println("============================================");
        System.out.println(">>> MOOD CONTROLLER /log endpoint called!");
        System.out.println(">>> Received values: q1=" + q1 + ", q2=" + q2 + ", q3=" + q3 + ", q4=" + q4 + ", q5=" + q5);
        System.out.println("============================================");
        
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            System.out.println(">>> ERROR: userId is null, redirecting to login");
            return "redirect:/login";
        }
        
        if (q1 == null || q2 == null || q3 == null || q4 == null || q5 == null) {
            System.out.println(">>> ERROR: One or more quiz answers is null!");
            redirectAttributes.addFlashAttribute("moodMessage", "Please answer all questions.");
            if (userRole != null) {
                if (userRole.equalsIgnoreCase("admin")) return "redirect:/admin";
                else if (userRole.equalsIgnoreCase("advisor")) return "redirect:/advisor";
                else if (userRole.equalsIgnoreCase("student")) return "redirect:/student";
            }
            return "redirect:/login";
        }

        // Get the user from database
        User user = userService.getUserById(userId);
        if (user == null) {
            System.out.println(">>> ERROR: User not found for userId: " + userId);
            return "redirect:/login";
        }
        
        System.out.println(">>> User found: " + user.getName());
        System.out.println(">>> User.getId(): " + user.getId());
        System.out.println(">>> User.getEmail(): " + user.getEmail());
        System.out.println(">>> User object class: " + user.getClass().getName());
        System.out.println(">>> User hashcode: " + user.hashCode());

        // Check if already logged in THIS SESSION (not today, but this login session)
        Boolean hasLoggedThisSession = (Boolean) session.getAttribute("moodLoggedToday");
        System.out.println(">>> Has logged mood this session: " + hasLoggedThisSession);
        
        if (hasLoggedThisSession != null && hasLoggedThisSession) {
            System.out.println(">>> Mood already logged in this session");
            redirectAttributes.addFlashAttribute("moodMessage", "You have already logged your mood for this session!");
        } else {
            System.out.println(">>> Calling logMoodFromQuiz with values: " + q1 + "," + q2 + "," + q3 + "," + q4 + "," + q5);
            // Log the mood from quiz
            try {
                moodLogService.logMoodFromQuiz(user, q1, q2, q3, q4, q5);
                System.out.println(">>> Mood logged successfully!");
            } catch (Exception e) {
                System.out.println(">>> ERROR saving mood: " + e.getMessage());
                e.printStackTrace();
            }
            
            // CRITICAL: Set session attribute to indicate mood has been logged
            session.setAttribute("moodLoggedToday", true);
            session.setAttribute("moodSkippedToday", false); // Reset skip flag
            
            redirectAttributes.addFlashAttribute("moodMessage", "Thank you! Your mental wellness has been recorded.");
        }

        // Redirect to appropriate landing page based on role
        if (userRole != null) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            } else if (userRole.equalsIgnoreCase("student")) {
                return "redirect:/student";
            }
        }
        
        return "redirect:/login";
    }

    /**
     * API endpoint to check if user has logged mood today
     * Used by AJAX calls from the frontend
     */
    @GetMapping("/check")
    @ResponseBody
    public boolean checkMoodLogged(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        
        if (userId == null) {
            return true; // Return true to prevent showing modal if not logged in
        }
        
        return moodLogService.hasLoggedMoodToday(userId);
    }

    /**
     * Skip mood logging for today
     * Sets a session flag to prevent the modal from showing again during this session
     */
    @PostMapping("/skip")
    public String skipMoodLog(HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Set session attribute to skip mood log for this session
        session.setAttribute("moodSkippedToday", true);
        
        redirectAttributes.addFlashAttribute("moodMessage", "You can log your mood anytime from your profile.");
        
        // Redirect to appropriate landing page based on role
        if (userRole != null) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            } else if (userRole.equalsIgnoreCase("student")) {
                return "redirect:/student";
            }
        }
        
        return "redirect:/login";
    }
}
