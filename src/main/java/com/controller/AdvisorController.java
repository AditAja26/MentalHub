package com.controller;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.transaction.annotation.Transactional;

import com.services.AnalysisService;
import com.services.UserService;
import com.model.User;

@Controller
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService;

    /**
     * UC000: Advisor Landing Page
     * Pulls Ahmed Ali's name (or whoever is logged in) from the session.
     */
    @GetMapping(value = { "", "/", "/home" })
    public String showAdvisorLandingPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        
        if (user == null) {
            return "redirect:/login";
        }

        // Dynamically set the name from the logged-in user object
        model.addAttribute("advisorName", user.getName()); 
        return "mainPages/advisorLandingPage";
    }

    /**
     * UC002: Monitor Dashboard
     */
    @GetMapping("/monitor")
    public String showMonitorDashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        List<User> students = userService.getUsersByRole("student");
        model.addAttribute("users", students);
        
        return "monitorAndAnalysisModule/monitorDashboard";
    }

    /**
     * UC010: Generate Report for a specific student
     * Optimized to use efficient JOIN FETCH query
     */
    @Transactional(readOnly = true)
    @GetMapping("/report")
    public String showReport(@RequestParam(name = "studentId") Long studentId, 
                             HttpSession session, 
                             Model model) {
        
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        // Use optimized method that fetches everything in one query
        User student = userService.getUserByIdWithDetails(studentId);
        
        if (student == null) {
            return "redirect:/advisor/monitor";
        }

        // No need to manually trigger lazy loading - everything is already loaded!
        
        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        
        model.addAttribute("user", student); 
        model.addAttribute("stats", analysis);
        model.addAttribute("chartData", analysis.get("weeklyMoods")); 
        model.addAttribute("reportDate", new java.util.Date());
        
        return "monitorAndAnalysisModule/advisorReport";
    }

    @GetMapping("/appointment")
    public String showAppointment(HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }
        return "studentSupportModule/appointmentPage";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testConnection() {
        return "<h1>Controller is ALIVE!</h1><p>Ahmed Ali's Advisor Dashboard is ready.</p>";
    }

    // ==========================================
    //           ADVISOR PROFILE MANAGEMENT
    // ==========================================

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        
        if (userId == null) {
            return "redirect:/login";
        }
        
        // Redirect non-advisors to their appropriate dashboard
        if (userRole != null && !userRole.equalsIgnoreCase("advisor")) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("student")) {
                return "redirect:/student";
            }
        }
        
        User user = userService.getUserById(userId);
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "advisorModule/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "advisorModule/editProfile";
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
        return "redirect:/advisor";
    }
}

