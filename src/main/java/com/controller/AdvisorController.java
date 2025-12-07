package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.model.User;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/advisor")
public class AdvisorController {
    
    @GetMapping(value = {"", "/"})
    public String showStudentLandingPage(Model model) {
        // User user = userService.getUserById(CURRENT_USER_ID);
        // model.addAttribute("advisorName", user != null ? user.getName() : "Student");
        return "mainPages/advisorLandingPage";
    }

    @GetMapping("/appointment")
    public String showAppointment() {
        return "#";
    }

    @GetMapping("/report")
    public String showReport() {
        return "#";
    }
    
}
