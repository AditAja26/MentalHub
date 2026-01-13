package com.controller;

import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.model.Appointment;
import com.model.User;
import com.model.MoodLog;
import com.services.AppointmentService;
import com.services.UserService;
import com.services.NotificationService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired 
    private AppointmentService appointmentService;

    @Autowired
    private NotificationService notificationService;

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
        
        // Add unread notification count to session
        if (userId != null) {
            long unreadCount = notificationService.getUnreadCount(userId);
            session.setAttribute("unreadCount", unreadCount);
        }
        
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
        
        // Fetch all advisors from the database
        List<User> advisors = userService.getUsersByRole("advisor");
        
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("advisors", advisors);
        return "studentSupportModule/BookAppointmentPage";
    }

    // --- UPDATED METHOD ---
    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute("appointment") Appointment appointment, HttpSession session) {
        // 1. Get the student FIRST
        User student = getSessionUser(session);
        
        if (student == null) {
            return "redirect:/login";
        }
        
        // 2. Set the missing fields BEFORE saving
        appointment.setStudent(student);        // <--- Links the appointment to this student
        appointment.setStatus("PENDING");       // <--- Sets status so Advisor can Accept/Reject later
        
        // 3. NOW Save the appointment
        appointmentService.addAppointment(appointment);
        
        // 4. Create notification for the student
        String studentMessage = String.format("Your appointment with %s on %s at %s has been successfully booked.", 
            appointment.getCounselor(), appointment.getDate(), appointment.getTime());
        
        notificationService.createNotification(
            student.getId(), 
            "Appointment Confirmed", 
            studentMessage, 
            "appointment"
        );
        
        // 5. Find the advisor and create notification for them
        List<User> advisors = userService.getUsersByRole("advisor");
        for (User advisor : advisors) {
            // Check if names match (assuming 'counselor' is the name string)
            if (advisor.getName().equals(appointment.getCounselor())) {
                String advisorMessage = String.format("New appointment request from %s on %s at %s. Reason: %s", 
                    student.getName(), appointment.getDate(), appointment.getTime(), appointment.getReason());
                
                notificationService.createNotification(
                    advisor.getId(), 
                    "New Appointment Booked", 
                    advisorMessage, 
                    "appointment"
                );
                break; // Stop loop once advisor is found
            }
        }
        
        return "redirect:/student/appointment?success";
    }

    @GetMapping("/counseling")
    public String showCounseling(HttpSession session) {
        if (getSessionUser(session) == null) return "redirect:/login";
        return "studentSupportModule/AttendCounselingPage";
    }
}