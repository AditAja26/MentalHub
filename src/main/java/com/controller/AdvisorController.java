package com.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.services.AnalysisService;

@Controller
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AnalysisService analysisService;

    // UC000: Advisor Landing Page (The Card View)
    @GetMapping(value = { "", "/", "/home" })
    public String showAdvisorLandingPage(Model model) {
        return "mainPages/advisorLandingPage";
    }

    // UC002: Monitor Dashboard (Table View of Student Progress)
    @GetMapping("/monitor")
    public String showMonitorDashboard(@RequestParam(name = "studentId", defaultValue = "9") Long studentId,
            Model model) {
        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        model.addAttribute("stats", analysis);
        return "monitorAndAnalysisModule/monitorDashboard";
    }

    // UC010: Generate Report (Formal Report View)
    @GetMapping("/report")
    public String showReport(@RequestParam(name = "studentId", defaultValue = "9") Long studentId,
            Model model) {
        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        model.addAttribute("stats", analysis);
        model.addAttribute("reportDate", new java.util.Date());
        return "monitorAndAnalysisModule/advisorReport";
    }

    @GetMapping("/appointment")
    public String showAppointment() {
        // Redirecting to the student support module views
        return "studentSupportModule/appointmentPage";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testConnection() {
        return "<h1>Controller is ALIVE!</h1><p>If you see this, the problem is your HTML file path.</p>";
    }
}