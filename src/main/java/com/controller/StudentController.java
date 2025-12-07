package com.controller;

import com.model.User;
import com.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private UserService userService;

    private static final Long CURRENT_USER_ID = 9L;

    @GetMapping(value = {"", "/"})
    public String showStudentLandingPage(Model model) {
        User user = userService.getUserById(CURRENT_USER_ID);
        model.addAttribute("studentName", user != null ? user.getName() : "Student");
        return "mainPages/studentLandingPage";
    }

    @GetMapping("/appointment")
    public String showAppointment(Model model) {
        return "redirect:/student";
    }

    @GetMapping("/counseling")
    public String showCounseling(Model model) {
        return "redirect:/student";
    }
}
