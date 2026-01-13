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
import org.springframework.transaction.annotation.Transactional;

import com.services.AnalysisService;
import com.services.UserService;
import com.model.User;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService;

    // UC000: Advisor Landing Page
    @GetMapping(value = { "", "/", "/home" })
    public String showAdvisorLandingPage(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        if (userName == null) {
            return "redirect:/login";
        }
        model.addAttribute("advisorName", userName); 
        return "mainPages/advisorLandingPage";
    }

    // UC002: Monitor Dashboard (Shows the list of all students)
    @GetMapping("/monitor")
    public String showMonitorDashboard(Model model) {
        // Fetch all students from your service
        List<User> students = userService.getUsersByRole("student");
        
        // Pass the list to the HTML
        model.addAttribute("users", students);
        
        return "monitorAndAnalysisModule/monitorDashboard";
    }

    // UC010: Generate Report (Shows details for ONE specific student)
    // @Transactional ensures the database session stays open to load Lazy collections (Goals)
    @Transactional(readOnly = true)
    @GetMapping("/report")
    public String showReport(@RequestParam(name = "studentId") Long studentId, Model model) {
        // 1. Get the specific student info
        User student = userService.getUserById(studentId);
        
        if (student == null) {
            return "redirect:/advisor/monitor";
        }

        // 2. Force load the Goals list (Lazy Loading Fix)
        if (student.getGoals() != null) {
            student.getGoals().size(); 
        }
        
        // 3. Get the analysis/stats for this specific student
        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        
        // 4. Add everything to the model
        model.addAttribute("user", student); 
        model.addAttribute("stats", analysis);
        
        // 5. EXTRACT CHART DATA: This makes the graph unique per student
        // 'weeklyMoods' must be the key used in your AnalysisService
        model.addAttribute("chartData", analysis.get("weeklyMoods")); 
        
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