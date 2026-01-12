package com.controller;

import java.util.Map;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.services.AnalysisService;
import com.services.UserService;
import com.model.User;

@Controller
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService; // Added this to fetch student lists

    // UC000: Advisor Landing Page
    @GetMapping(value = { "", "/", "/home" })
    public String showAdvisorLandingPage(Model model) {
        // Optional: Pass the advisor's name to the landing page
        model.addAttribute("advisorName", "Hakimi"); 
        return "mainPages/advisorLandingPage";
    }

    // UC002: Monitor Dashboard (Shows the list of all students)
    @GetMapping("/monitor")
    public String showMonitorDashboard(Model model) {
        // Fetch all students from your service
        List<User> students = userService.getUsersByRole("student");
        
        // Pass the list to the HTML (Thymeleaf uses 'users')
        model.addAttribute("users", students);
        
        return "monitorAndAnalysisModule/monitorDashboard";
    }

    // UC010: Generate Report (Shows details for ONE specific student)
    @GetMapping("/report")
    public String showReport(@RequestParam(name = "studentId") Long studentId, Model model) {
        // 1. Get the specific student info
        User student = userService.getUserById(studentId);
        
        // 2. Get the analysis/stats for this student
        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        
        // 3. Add everything to the model
        model.addAttribute("user", student); // Used for name/ID in report
        model.addAttribute("stats", analysis); // Used for mood/stress stats
        model.addAttribute("reportDate", new java.util.Date());
        
        return "monitorAndAnalysisModule/advisorReport";
    }

    @GetMapping("/appointment")
    public String showAppointment() {
        return "studentSupportModule/appointmentPage";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testConnection() {
        return "<h1>Controller is ALIVE!</h1><p>Paths are working correctly.</p>";
    }
}