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
import com.model.CounselingSession;
import com.services.AppointmentService;
import com.services.UserService;
import com.services.NotificationService;
import com.services.CounselingSessionService; // Required for sessions

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired 
    private AppointmentService appointmentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CounselingSessionService sessionService; // Added service

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
        
        if (userRole != null && !userRole.equalsIgnoreCase("student")) {
            if (userRole.equalsIgnoreCase("admin")) {
                return "redirect:/admin";
            } else if (userRole.equalsIgnoreCase("advisor")) {
                return "redirect:/advisor";
            }
        }
        
        User user = userService.getUserById(userId);
        model.addAttribute("studentName", user != null ? user.getName() : "Student");
        
        if (userId != null) {
            long unreadCount = notificationService.getUnreadCount(userId);
            session.setAttribute("unreadCount", unreadCount);
        }
        
        return "mainPages/studentLandingPage";
    }

    // --- WELLNESS SESSIONS LOGIC ---

    @GetMapping("/sessions")
    public String listSessions(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // Fetch fresh data to avoid LazyInitialization / Proxy issues
        User currentUser = userService.getUserById(userId);
        List<CounselingSession> allSessions = sessionService.getAllSessions();

        model.addAttribute("sessions", allSessions);
        model.addAttribute("currentUser", currentUser);
        return "studentModule/sessionList";
    }

    @PostMapping("/sessions/join")
    public String joinSession(@RequestParam("sessionId") Long sessionId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        CounselingSession cSession = sessionService.getSessionById(sessionId);
        User student = userService.getUserById(userId);

        if (cSession != null && student != null && !cSession.isFull()) {
            // Check if student is already in the list
            if (!cSession.getAttendees().contains(student)) {
                cSession.getAttendees().add(student);
                sessionService.updateSession(cSession); // Persist change
            }
            return "redirect:/student/sessions?joined=true";
        }
        
        return "redirect:/student/sessions?joined=false";
    }

    @PostMapping("/sessions/leave")
    public String leaveSession(@RequestParam("sessionId") Long sessionId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        CounselingSession cSession = sessionService.getSessionById(sessionId);
        User student = userService.getUserById(userId);

        if (cSession != null && student != null) {
            // Remove the student from the session's attendee list
            cSession.getAttendees().remove(student);
            sessionService.updateSession(cSession);
        }
        
        return "redirect:/student/sessions?left=true";
    }

    // --- REMAINING METHODS ---

    @GetMapping("/analysis")
    public String showStudentAnalysis(HttpSession session, Model model) {
        User user = getSessionUser(session);
        if (user == null) return "redirect:/login";

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
        
        List<User> advisors = userService.getUsersByRole("advisor");
        model.addAttribute("appointment", new Appointment());
        model.addAttribute("advisors", advisors);
        return "studentSupportModule/BookAppointmentPage";
    }

    @PostMapping("/book-appointment")
    public String bookAppointment(@ModelAttribute("appointment") Appointment appointment, HttpSession session) {
        User student = getSessionUser(session);
        if (student == null) return "redirect:/login";
        
        appointment.setStudent(student);
        appointment.setStatus("PENDING");
        appointmentService.addAppointment(appointment);
        
        String studentMessage = String.format("Your appointment with %s on %s at %s has been successfully booked.", 
            appointment.getCounselor(), appointment.getDate(), appointment.getTime());
        
        notificationService.createNotification(student.getId(), "Appointment Confirmed", studentMessage, "appointment");
        
        List<User> advisors = userService.getUsersByRole("advisor");
        for (User advisor : advisors) {
            if (advisor.getName().equals(appointment.getCounselor())) {
                String advisorMessage = String.format("New appointment request from %s on %s at %s.", 
                    student.getName(), appointment.getDate(), appointment.getTime());
                
                notificationService.createNotification(advisor.getId(), "New Appointment Booked", advisorMessage, "appointment");
                break;
            }
        }
        
        return "redirect:/student/appointment?success";
    }

    @GetMapping("/counseling")
    public String showCounseling(HttpSession session) {
        if (getSessionUser(session) == null) return "redirect:/login";
        // Option: Redirect to the sessions list directly
        return "redirect:/student/sessions";
    }
}