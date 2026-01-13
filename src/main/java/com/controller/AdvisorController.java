package com.controller;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import com.services.AnalysisService;
import com.services.UserService;
import com.services.AppointmentService;
import com.services.NotificationService;
import com.services.CounselingSessionService;
import com.model.User;
import com.model.Appointment;
import com.model.CounselingSession;

@Controller
@RequestMapping("/advisor")
public class AdvisorController {

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private CounselingSessionService sessionService;

    /**
     * UC000: Advisor Landing Page
     */
    @GetMapping(value = { "", "/", "/home" })
    public String showAdvisorLandingPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("advisorName", user.getName());
        long unreadCount = notificationService.getUnreadCount(user.getId());
        session.setAttribute("unreadCount", unreadCount);
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
     */
    @Transactional(readOnly = true)
    @GetMapping("/report")
    public String showReport(@RequestParam(name = "studentId") Long studentId, 
                             HttpSession session, 
                             Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";

        User student = userService.getUserByIdWithDetails(studentId);
        if (student == null) return "redirect:/advisor/monitor";

        Map<String, Object> analysis = analysisService.analyzeUserProgress(studentId);
        
        model.addAttribute("user", student); 
        model.addAttribute("stats", analysis);
        model.addAttribute("chartData", analysis.get("weeklyMoods")); 
        model.addAttribute("reportDate", new java.util.Date());
        
        return "monitorAndAnalysisModule/advisorReport";
    }

    /**
     * View Appointments
     */
    @GetMapping("/appointment")
    public String showAppointment(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        
        String advisorName = user.getName();
        List<Appointment> appointments = appointmentService.getAppointmentsByAdvisor(advisorName);
        
        int totalAppointments = appointments.size();
        int upcomingAppointments = 0;
        
        for(Appointment appt : appointments) {
            if(!"COMPLETED".equals(appt.getStatus()) && !"REJECTED".equals(appt.getStatus())) {
                upcomingAppointments++;
            }
        }
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("todayAppointments", 0); 
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        
        return "advisorModule/appointmentManagement";
    }

    /**
     * Accept or Reject Appointment
     */
    @PostMapping("/appointment/update-status")
    public String updateAppointmentStatus(@RequestParam("appointmentId") Long appointmentId, 
                                          @RequestParam("status") String status,
                                          HttpSession session) {
        User advisor = (User) session.getAttribute("loggedInUser");
        if (advisor == null) return "redirect:/login";

        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        if (appointment != null) {
            appointment.setStatus(status);
            appointmentService.addAppointment(appointment);
            
            User student = appointment.getStudent();
            if (student != null) {
                String message = "ACCEPTED".equalsIgnoreCase(status) 
                    ? "Good news! Your appointment with " + advisor.getName() + " has been confirmed."
                    : "Update: Your appointment with " + advisor.getName() + " was declined.";
                
                notificationService.createNotification(student.getId(), "Appointment Update", message, "appointment");
            }
        }
        return "redirect:/advisor/appointment";
    }

    @GetMapping("/appointment/complete/{id}")
    public String completeAppointment(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        appointmentService.deleteAppointment(id);
        return "redirect:/advisor/appointment?completed=true";
    }

    @GetMapping("/appointment/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        appointmentService.deleteAppointment(id);
        return "redirect:/advisor/appointment?cancelled=true";
    }

    // ==========================================
    //       COUNSELING SESSION MANAGEMENT
    // ==========================================

    @GetMapping("/sessions")
    public String manageSessions(HttpSession session, Model model) {
        // 1. Get Session User
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) return "redirect:/login";

        // 2. REFRESH User from DB (Fixes 'Detached Entity' issues)
        User advisor = userService.getUserById(sessionUser.getId());
        
        // 3. Get Sessions
        List<CounselingSession> mySessions = sessionService.getSessionsByAdvisor(advisor);
        
        model.addAttribute("sessions", mySessions);
        return "advisorModule/sessionManagement";
    }

    @GetMapping("/sessions/create")
    public String createSessionForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInUser") == null) return "redirect:/login";
        model.addAttribute("counselingSession", new CounselingSession());
        return "advisorModule/createSession";
    }

    @PostMapping("/sessions/save")
    public String saveSession(@ModelAttribute CounselingSession counselingSession, HttpSession session) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) return "redirect:/login";

        // Refresh user to be safe
        User advisor = userService.getUserById(sessionUser.getId());

        counselingSession.setAdvisor(advisor);
        sessionService.createSession(counselingSession);
        return "redirect:/advisor/sessions";
    }

    @GetMapping("/sessions/delete/{id}")
    public String deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
        return "redirect:/advisor/sessions";
    }

    // ==========================================
    //          ADVISOR PROFILE MANAGEMENT
    // ==========================================

    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "advisorModule/profile";
    }

    @GetMapping("/profile/edit")
    public String showEditProfile(Model model, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";
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
        if (userId == null) return "redirect:/login";
        
        User updatedUser = new User();
        updatedUser.setName(name);
        updatedUser.setEmail(email);
        updatedUser.setPhone(phone);
        if (password != null && !password.isEmpty()) {
            updatedUser.setPassword(password);
        }
        
        User user = userService.updateUser(userId, updatedUser);
        if (user != null) {
            session.setAttribute("userName", name);
            session.setAttribute("loggedInUser", user);
        }
        return "redirect:/advisor";
    }
}