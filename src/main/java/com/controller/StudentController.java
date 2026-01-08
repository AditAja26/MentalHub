package com.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.User;
import com.services.AnalysisService;
import com.services.UserService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    private static final Long CURRENT_USER_ID = 9L;

    @GetMapping(value = { "", "/" })
    public String showStudentLandingPage(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        model.addAttribute("studentName", user != null ? user.getName() : "Student");
        return "mainPages/studentLandingPage";
    }

    @GetMapping("/appointment")
    public String showAppointment(Model model) {
        return "studentSupportModule/BookAppointmentPage";
    }

    @GetMapping("/counseling")
    public String showCounseling(Model model) {
        return "studentSupportModule/AttendCounselingPage";
    }

    @Autowired
    private AnalysisService analysisService;

    @GetMapping("/student/monitor")
    public String showMonitorDashboard(Model model) {
        // For now, we use Hakimi's ID (9L) which has data in GoalService
        Long currentUserId = 9L;

        Map<String, Object> analysis = analysisService.analyzeUserProgress(currentUserId);
        model.addAttribute("stats", analysis);

        return "monitoringModule/monitorDashboard";
    }
}
