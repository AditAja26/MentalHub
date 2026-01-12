package com.controller;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.Appointment;
import com.model.User;
import com.services.AppointmentService;
import com.services.UserService;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    @Autowired 
    private AppointmentService appointmentService;

    private static final Long CURRENT_USER_ID = 9L;

    @GetMapping(value = { "", "/" })
    public String showStudentLandingPage(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        model.addAttribute("studentName", user != null ? user.getName() : "Student");
        return "mainPages/studentLandingPage";
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
        appointmentService.saveAppointment(appointment);
        return "redirect:/student/appointment?success";
    }
}
