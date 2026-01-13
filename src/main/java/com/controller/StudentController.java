package com.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.model.Appointment;
import com.model.User;
import com.model.MoodLog;
import com.services.AppointmentService;
import com.services.UserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired 
    private AppointmentService appointmentService;

    @GetMapping(value = { "", "/" })
    public String showStudentLandingPage(Model model, HttpSession session) {
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
        model.addAttribute("studentName", user != null ? user.getName() : "Student");
        return "mainPages/studentLandingPage";
    }

    /**
     * This method fixes the blank "Student Analysis" page.
     * It fetches the user, their goals, and their trend data.
     */
    @GetMapping("/analysis")
    public String showStudentAnalysis(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/login";
        }
        
        User user = userService.getUserById(userId);
        
        if (user != null) {
            // 1. Calculate Mood Average for the blue box
            double average = 0.0;
            List<MoodLog> moods = user.getMoodLogs();
            if (moods != null && !moods.isEmpty()) {
                average = moods.stream().mapToDouble(MoodLog::getScore).average().orElse(0.0);
            }

            // 2. Add data to the page model
            model.addAttribute("user", user);
            model.addAttribute("goals", user.getGoals()); // Accessed via user_goals join table
            model.addAttribute("moodAverage", String.format("%.1f", average));
            model.addAttribute("moodLogs", moods); // Data for the trend chart
        }

        return "mainPages/studentAnalysisPage"; // Ensure this matches your JSP file name
    }

    @GetMapping("/appointment")
    public String showAppointment(Model model) {
        model.addAttribute("appointment", new Appointment()); 
        return "studentSupportModule/BookAppointmentPage";
    }

    @GetMapping("/counseling")
    public String showCounseling(Model model) {
        return "studentSupportModule/AttendCounselingPage";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute("appointment") Appointment appointment) {
        appointmentService.addAppointment(appointment);
        return "redirect:/student/appointment?success";
    }
}