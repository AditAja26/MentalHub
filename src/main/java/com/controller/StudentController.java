package com.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.Appointment;
import com.model.CounsellingSession;
import com.model.MoodLog;
import com.model.User;
import com.services.AppointmentService;
import com.services.CounsellingSessionService;
import com.services.UserService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired 
    private AppointmentService appointmentService;

    @Autowired
    private CounsellingSessionService counsellingSessionService;

    // Helper to get the user from session safely
    private User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("loggedInUser");
    }

    

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

    @GetMapping("/analysis")
    public String showStudentAnalysis(HttpSession session, Model model) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

        // Fetch fresh data for the logged-in user
        User currentUser = userService.getUserById(user.getId());

        if (currentUser != null) {
            double average = 0.0;
            List<MoodLog> moods = currentUser.getMoodLogs();
            if (moods != null && !moods.isEmpty()) {
                average = moods.stream().mapToDouble(MoodLog::getScore).average().orElse(0.0);
            }

            model.addAttribute("user", currentUser);
            model.addAttribute("goals", currentUser.getGoals());
            model.addAttribute("moodAverage", String.format("%.1f", average));
            model.addAttribute("moodLogs", moods);
            
            String stress = (average > 3.5) ? "LOW" : (average > 2.5) ? "MODERATE" : "HIGH";
            model.addAttribute("stressLevel", stress);
        }

        return "mainPages/studentAnalysisPage";
    }

    @GetMapping("/appointment")
    public String showAppointment(HttpSession session, Model model) {
        if (getSessionUser(session) == null) return "redirect:/login";
        model.addAttribute("appointment", new Appointment()); 
        return "studentSupportModule/BookAppointmentPage";
    }

    @GetMapping("/counseling")
    public String showCounseling(Model model) {
        List<CounsellingSession> sessions = counsellingSessionService.getAllSessions(); 
        model.addAttribute("sessions", sessions);
        return "studentSupportModule/AttendCounselingPage";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute("appointment") Appointment appointment) {
        appointmentService.addAppointment(appointment);
        return "redirect:/student/appointment?success";
    }

    @GetMapping("/counseling")
    public String showCounseling(HttpSession session) {
        if (getSessionUser(session) == null) return "redirect:/login";
        return "studentSupportModule/AttendCounselingPage";
    }
}