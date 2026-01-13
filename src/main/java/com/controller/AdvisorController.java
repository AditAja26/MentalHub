package com.controller;

import java.util.Map;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.transaction.annotation.Transactional;

import com.services.AnalysisService;
import com.services.UserService;
import com.services.AppointmentService;
import com.services.NotificationService;
import com.model.User;
import com.model.Appointment; // Added this import

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
        
        if (session.getAttribute("loggedInUser") == null) {
            return "redirect:/login";
        }

        User student = userService.getUserByIdWithDetails(studentId);
        
        if (student == null) {
            return "redirect:/advisor/monitor";
        }

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
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get all appointments for this advisor
        String advisorName = user.getName();
        List<Appointment> appointments = appointmentService.getAppointmentsByAdvisor(advisorName);
        
        // Calculate statistics
        int totalAppointments = appointments.size();
        int todayAppointments = 0; 
        int upcomingAppointments = 0;
        
        // Simple logic to count (you can refine this later)
        for(Appointment appt : appointments) {
            if(!"COMPLETED".equals(appt.getStatus()) && !"REJECTED".equals(appt.getStatus())) {
                upcomingAppointments++;
            }
        }
        
        model.addAttribute("appointments", appointments);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        
        return "advisorModule/appointmentManagement";
    }

    /**
     * NEW FEATURE: Accept or Reject Appointment
     * This method handles the buttons from the appointmentManagement page.
     */
    @PostMapping("/appointment/update-status")
    public String updateAppointmentStatus(@RequestParam("appointmentId") Long appointmentId, 
                                          @RequestParam("status") String status,
                                          HttpSession session) {
        
        User advisor = (User) session.getAttribute("loggedInUser");
        if (advisor == null) {
            return "redirect:/login";
        }

        // 1. Fetch the appointment
        // Ensure your AppointmentService has this method!
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        
        if (appointment != null) {
            // 2. Update the status
            appointment.setStatus(status); // "ACCEPTED" or "REJECTED"
            appointmentService.addAppointment(appointment); // Save the change
            
            // 3. Notify the Student
            User student = appointment.getStudent();
            if (student != null) {
                String message = "";
                if ("ACCEPTED".equalsIgnoreCase(status)) {
                    message = String.format("Good news! Your appointment with %s on %s at %s has been confirmed.", 
                        advisor.getName(), appointment.getDate(), appointment.getTime());
                } else {
                    message = String.format("Update: Your appointment with %s on %s was declined. Please try booking another time.", 
                        advisor.getName(), appointment.getDate());
                }
                
                notificationService.createNotification(
                    student.getId(),
                    "Appointment Update", 
                    message, 
                    "appointment"
                );
            }
        }
        
        return "redirect:/advisor/appointment";
    }

    // Keep these if you still want to delete appointments entirely
    // or you can remove them if "update-status" replaces them.
    @GetMapping("/appointment/complete/{id}")
    public String completeAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        appointmentService.deleteAppointment(id);
        return "redirect:/advisor/appointment?completed=true";
    }

    @GetMapping("/appointment/cancel/{id}")
    public String cancelAppointment(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/login";
        appointmentService.deleteAppointment(id);
        return "redirect:/advisor/appointment?cancelled=true";
    }

    @GetMapping("/test")
    @ResponseBody
    public String testConnection() {
        return "<h1>Controller is ALIVE!</h1><p>Advisor Dashboard is ready.</p>";
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